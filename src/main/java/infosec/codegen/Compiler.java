package infosec.codegen;

import infosec.AST.*;
import infosec.AST.Expression.*;
import infosec.AST.Statement.*;
import infosec.AST.Statement.FunctionDecStatement.FunctionArg;
import infosec.parser.*;
import infosec.lexer.*;

import java.nio.file.*;
import java.util.*;

public class Compiler {
    private CodeGen codeGen;
    private Parser parser;
    private Lexer lexer;
    private int debugLevel = 2;
    private HashMap<String, JImportStatement> jImports;

    public Compiler(String filename) {
        Path path = Paths.get(filename);
        String file = path.getFileName().toString();
        String name = file.substring(0, file.lastIndexOf("."));
        this.codeGen = new CodeGen(name);
        this.lexer = new Lexer(filename);
        this.parser = new Parser(this.lexer);
        this.jImports = new HashMap<String, JImportStatement>();
    }

    public void debug(int level, String msg) {
        if ( debugLevel >= level ) {
            System.out.println("[DEBUG] " + msg);
        }
    }

    public Type locateType(Expression expr) {
        debug(2, "Getting type of: " + expr);

        if ( expr instanceof NumberExpression ) {
            return new Type("int");
        }
        else if ( expr instanceof FloatExpression ) {
            return new Type("Float");
        }
        else if ( expr instanceof StringExpression ) {
            return new Type("string");
        }
        else if ( expr instanceof InfixExpression ) {
            InfixExpression ifx = (InfixExpression) expr;

            Type a = locateType(ifx.getLHS());

            return a;
        }
        else if ( expr instanceof SuffixExpression ) {
            return locateType(((SuffixExpression) expr).getLHS());
        }
        else if ( expr instanceof PrefixExpression ) {
            return locateType(((PrefixExpression) expr).getRHS());
        }
        else if ( expr instanceof VariableExpression ) {
            String name = ((VariableExpression) expr).getName();

            if ( this.jImports.containsKey(name) ) {
                return new Type(this.jImports.get(name).getObject().replace("/", "."), 0);
            }

            return new Type(this.codeGen.getVariableType(name), this.codeGen.getArrayDepth(name));
        }
        else if ( expr instanceof FunctionCallExpression ) {
            String name = ((FunctionCallExpression) expr).getName();
            return new Type(this.codeGen.getFunction(name)[0].getReturn());
        }
        else if ( expr instanceof MethodCallExpression ) {
            MethodCallExpression call = (MethodCallExpression) expr;

            Expression lhs = call.getLHS();
            Type type = locateType(lhs);

            VirtualMethod[] tmps = this.codeGen.getMethod(type.getBasicType(), call.getName());

            if ( tmps.length < 1 ) {
                return null;
            }

            return new Type(tmps[0].getReturn(), 0);
        }
        else if ( expr instanceof ArrayDereferenceExpression ) {
            Type t = locateType(((ArrayDereferenceExpression) expr).getLHS());
            return new Type(t.getBasicType(), t.getArrayDepth() - 1);
        }
        else if ( expr instanceof NewObjectExpression ) {
            NewObjectExpression obj = (NewObjectExpression) expr;

            String name = obj.getName();

            if ( jImports.containsKey(name) ) {
                name = jImports.get(name).getObject();
            }

            return new Type(name, 0);
        }

        return null;
    }

    public String compileExpression(Expression expr) {
        debug(2, "Compiling expression: " + expr);

        if ( expr instanceof NumberExpression ) {
            debug(1, "Compiling a number expression.");
            NumberExpression num = (NumberExpression) expr;
            this.codeGen.pushInteger(num.getValue());
            return "int";
        }
        else if ( expr instanceof FloatExpression ) {
            debug(1, "Compiling a float expression.");
            FloatExpression num = (FloatExpression) expr;
            this.codeGen.pushFloat(num.getValue());
            return "float";
        }
        else if ( expr instanceof StringExpression ) {
            debug(1, "Compiling a String expression.");
            StringExpression str = (StringExpression) expr;
            this.codeGen.pushString(str.getValue());
            return "string";
        }
        else if ( expr instanceof InfixExpression ) {
            debug(1, "Compiling an infix expression");
            InfixExpression infix = (InfixExpression) expr;

            String lhs = compileExpression(infix.getLHS());
            String rhs = compileExpression(infix.getRHS());

            String op = infix.getOP();

            if ( !lhs.equals(rhs) && !(lhs.equals("string") || rhs.equals("string")) ) {
                this.codeGen.convert(rhs, lhs);
            }

            this.codeGen.operation(op, lhs);

            return lhs;
        }
        else if ( expr instanceof VariableExpression ) {
            String obj = ((VariableExpression) expr).getName();

            if ( this.jImports.containsKey(obj) ) {
                return this.jImports.get(obj).getObject().replace("/", ".");
            }

            if ( obj.indexOf("/") > -1 ) {
                debug(1, "Java object detected...");
                return obj.replace("/", ".");
            }

            this.codeGen.pushVariable(((VariableExpression) expr).getName());
            return this.codeGen.getVariableType(((VariableExpression) expr).getName());
        }
        else if ( expr instanceof FunctionCallExpression ) {
            debug(1, "Compiling a function call expression.");

            FunctionCallExpression call = (FunctionCallExpression)(expr);

            String name = call.getName();

            debug(1, "Function called: " + name);

            VirtualMethod[] tmps = this.codeGen.getFunction(name);

            if ( tmps.length < 1 ) {
                return "";
            }

            String ret = tmps[0].getReturn();

            debug(1, "Return type located: " + ret);

            VirtualMethod tmp = new VirtualMethod("", ret);

            for ( int i = 0; i < call.getArgCount(); i++ ) {
                Type t = locateType(call.getArg(i));
                tmp.addArg(i + " ", t.getBasicType(), t.getArrayDepth());
            }

            String desc = tmp.getDescriptor();

            int found = -1;

            debug(1, "The located descriptor was: " + desc);

            for ( int i = 0; i < tmps.length; i++ ) {
                debug(1, "Checking: " + tmps[i].getDescriptor());
                if ( tmps[i].getDescriptor().equals(desc) ) {
                    found = i;
                    break;
                }
            }

            if ( found == -1 ) {
                System.out.println(call + " was not declared.");
                return "";
            }

            this.codeGen.startFunctionCall(call.getName(), tmps[found].getDescriptor());

            for ( int i = 0; i < call.getArgCount(); i++ ) {
                compileExpression(call.getArg(i));
            }

            this.codeGen.endFunctionCall();

            return ret;
        }
        else if ( expr instanceof MethodCallExpression ) {
            debug(1, "Compiling a method call expression.");

            MethodCallExpression meth = (MethodCallExpression)(expr);

            Expression lhs = meth.getLHS();
            String obj = compileExpression(lhs);

            if ( obj.equals("string") ) {
                obj = "java/lang/String";
            }

            debug(1, "Searching " + obj + "->" + meth.getName());

            VirtualMethod[] tmps = this.codeGen.getMethod(obj, meth.getName());

            if ( tmps.length < 1 ) {
                return "";
            }

            String ret = tmps[0].getReturn();

            VirtualMethod tmp = new VirtualMethod("", ret);

            for ( int i = 0; i < meth.getArgCount(); i++ ) {
                Type t = locateType(meth.getArg(i));
                tmp.addArg(i + " ", t.getBasicType().replace("/", "."), t.getArrayDepth());
            }

            String desc = tmp.getDescriptor();
            int found = -1;

            for ( int i = 0; i < tmps.length; i++ ) {
                if ( tmps[i].getDescriptor().equals(desc) ) {
                    found = i;
                    break;
                }
            }

            if ( found == -1 ) {
                System.out.println(meth + " was not declared.");
                return "";
            }

            debug(1, "Resolved " + obj + "->" + meth.getName() + " to " + desc);

            this.codeGen.startMethodCall(obj.replace(".", "/"), meth.getName(), desc);

            for ( int i = 0; i < meth.getArgCount(); i++ ) {
                compileExpression(meth.getArg(i));
            }

            this.codeGen.endMethodCall();

            return ret;
        }
        else if ( expr instanceof FieldDereferenceExpression ) {
            debug(1, "Field dereference expression.");

            FieldDereferenceExpression deref = (FieldDereferenceExpression) expr;

            String type = compileExpression(deref.getLHS()).replace(".", "/");
            System.out.println(type);
            VirtualField field = this.codeGen.getField(type, deref.getName());
            String type2 = field.getType();

            if ( field.isStatic() ) {
                this.codeGen.pushStaticField(type.replace(".", "/"), deref.getName());
            }
            else {
                this.codeGen.pushField(type.replace(".", "/"), deref.getName());
            }

            return type2;
        }
        else if ( expr instanceof ArrayDereferenceExpression ) {
            ArrayDereferenceExpression ader = (ArrayDereferenceExpression) expr;

            String type = compileExpression(ader.getLHS());
            compileExpression(ader.getInner());

            this.codeGen.loadFromArray(type);
            return type;
        }
        else if ( expr instanceof ArrayExpression ) {
            ArrayExpression aex = (ArrayExpression) expr;

            if ( aex.getExpressionCount() == 0 ) {
                return "";
            }

            String type = locateType(aex.getExpression(0)).getBasicType();

            this.codeGen.pushInteger(aex.getExpressionCount());
            this.codeGen.pushNewArray(1, type);

            for ( int i = 0; i < aex.getExpressionCount(); i++ ) {
                this.codeGen.dup();
                this.codeGen.pushInteger(i);
                compileExpression(aex.getExpression(i));
                this.codeGen.startLastArrayStore(type);
                this.codeGen.endArrayStore();
            }

            return type;
        }
        else if ( expr instanceof PrefixExpression ) {
            PrefixExpression pfx = (PrefixExpression) expr;

            if ( pfx.getOP().equals("#") ) {
                Type type = locateType(pfx);

                debug(1, "Length operator on type of " + type);

                if ( type.getBasicType().equals("java/lang/String") && type.getArrayDepth() == 0 ) {
                    debug(1, "Compiling a string length.");

                    this.codeGen.startMethodCall("java/lang/String", "length", "()I");
                    compileExpression(pfx.getRHS());
                    this.codeGen.endMethodCall();
                }
                else if ( type.getArrayDepth() > 0) {
                    compileExpression(pfx.getRHS());
                    this.codeGen.pushArrayLength();
                }

                return "int";
            }
            else {
                String type = compileExpression(pfx.getRHS());

                this.codeGen.pushInteger(1);

                if ( pfx.getOP().equals("++") ) {
                    this.codeGen.operation("+", type);
                }
                else if ( pfx.getOP().equals("--") ) {
                    this.codeGen.operation("-", type);
                }

                if ( pfx.getRHS() instanceof VariableExpression ) {
                    String var = ((VariableExpression) pfx.getRHS()).getName();
                    this.codeGen.storeLastVariable(var, type);
                    return type;
                }
                else {
                    return "";
                }
            }
        }
        else if ( expr instanceof SuffixExpression ) {
            debug(1, "Compiling suffix expression.");

            SuffixExpression sfx = (SuffixExpression) expr;

            String type = compileExpression(sfx.getLHS());
            this.codeGen.pushInteger(1);

            if ( sfx.getOP().equals("++") ) {
                this.codeGen.operation("+", type);
            }
            else if ( sfx.getOP().equals("--") ) {
                this.codeGen.operation("-", type);
            }

            if ( sfx.getLHS() instanceof VariableExpression ) {
                debug(1, "Storing variable.");
                String var = ((VariableExpression) sfx.getLHS()).getName();
                this.codeGen.storeLastVariable(var, type);
            }
            else {
                return "";
            }

            return type;
        }
        else if ( expr instanceof NewObjectExpression ) {
            debug(1, "Compiling a new object expression.");

            NewObjectExpression call = (NewObjectExpression)(expr);

            String name = call.getName();

            if ( this.jImports.containsKey(name) ) {
                name = this.jImports.get(name).getObject();
            }

            VirtualMethod[] tmps = this.codeGen.getMethod(name, "<init>");

            if ( tmps.length < 1 ) {
                System.out.println("Unable to find any corresponding methods.");
                return "";
            }

            String ret = tmps[0].getReturn();

            debug(1, "Return type located: " + ret);

            VirtualMethod tmp = new VirtualMethod("", ret);

            for ( int i = 0; i < call.getArgCount(); i++ ) {
                Type t = locateType(call.getArg(i));
                tmp.addArg(i + " ", t.getBasicType(), t.getArrayDepth());
            }

            String desc = tmp.getDescriptor();

            int found = -1;

            debug(1, "The located descriptor was: " + desc);

            for ( int i = 0; i < tmps.length; i++ ) {
                if ( tmps[i].getDescriptor().equals(desc) ) {
                    found = i;
                    break;
                }
            }

            if ( found == -1 ) {
                System.out.println(call + " was not declared.");
                return "";
            }

            this.codeGen.startInitCall(name, tmps[found].getDescriptor());

            for ( int i = 0; i < call.getArgCount(); i++ ) {
                compileExpression(call.getArg(i));
            }

            this.codeGen.endInitCall();

            return name;
        }
        return "";
    }

    public void compileStatement(Statement stmt) {
        debug(2, "Compiling statement: " + stmt);

        if ( stmt instanceof FunctionDecStatement ) {
            debug(1, "Compiling a function declaration statement.");

            FunctionDecStatement dec = (FunctionDecStatement) stmt;

            this.codeGen.startFunction(dec.getName(), dec.getType().getBasicType(), dec.getType().getArrayDepth());

            for ( int i = 0; i < dec.getArgCount(); i++ ) {
                String name = dec.getArg(i).getName();
                String basicType = dec.getArg(i).getType().getBasicType();
                int arrayDepth =  dec.getArg(i).getType().getArrayDepth();

                this.codeGen.addFunctionArgument(name, basicType, arrayDepth);
            }

            Block block = dec.getBlock();

            for ( int i = 0; i < block.getStatementCount(); i++ ) {
                compileStatement(block.getStatement(i));
            }

            this.codeGen.endFunction();
        }
        else if ( stmt instanceof VariableDecStatement ) {
            debug(1, "Compiling a variable declaration statement.");

            VariableDecStatement dec = (VariableDecStatement) stmt;

            String type = compileExpression(dec.getValue());
            String ours = dec.getType().getBasicType();

            if ( ours.equals("void") ) {
                ours = type;
            }
            this.codeGen.storeLastVariable(dec.getName(), ours);
        }
        else if ( stmt instanceof ExpressionStatement ) {
            debug(1, "Compiling a expression statement.");

            ExpressionStatement exprstmt = (ExpressionStatement) stmt;

            if ( exprstmt.getType().equals("varset") ) {
                debug(1, "Variable set expression detected...");

                if ( exprstmt.getExpression() instanceof InfixExpression ) {
                    InfixExpression expr = (InfixExpression) exprstmt.getExpression();
                    Expression _lhs = expr.getLHS();
                    Expression _rhs = expr.getRHS();

                    if ( !(_lhs instanceof VariableExpression ) && !(_lhs instanceof ArrayDereferenceExpression) ) {
                        System.out.println("Failed to find a left hand side for a variable set expression.");
                        return;
                    }

                    if ( _lhs instanceof VariableExpression ) {
                        String lhs = ((VariableExpression) _lhs).getName();
                        String type = compileExpression(_rhs);
                        this.codeGen.storeLastVariable(lhs, type);
                    }
                    else if ( _lhs instanceof ArrayDereferenceExpression ) {
                        ArrayDereferenceExpression ader = (ArrayDereferenceExpression) _lhs;

                        String type = compileExpression(ader.getLHS());
                        compileExpression(ader.getInner());
                        compileExpression(_rhs);

                        this.codeGen.startLastArrayStore(type);
                        this.codeGen.endArrayStore();
                    }
                }
                else if ( exprstmt.getExpression() instanceof SuffixExpression || exprstmt.getExpression() instanceof PrefixExpression ) {
                    compileExpression(exprstmt.getExpression());
                }
            }
            else if ( exprstmt.getType().equals("fncall") ) {
                debug(1, "Function call expression detected...");
                compileExpression(exprstmt.getExpression());
            }
            else if ( exprstmt.getType().equals("return") ) {
                debug(1, "Return expression detected.");

                if ( exprstmt.getExpression() != null ) {
                    compileExpression(exprstmt.getExpression());
                }

                this.codeGen.returnVoid();
            }
        }
        else if ( stmt instanceof IfStatement ) {
            IfStatement ifstmt = (IfStatement) stmt;
            Expression expr = ifstmt.getExpression();

            if ( expr instanceof InfixExpression ) {
                InfixExpression tmp = (InfixExpression) expr;
                String a = compileExpression(tmp.getLHS());
                String b = compileExpression(tmp.getRHS());
                this.codeGen.startCondition(tmp.getOP(), a, true);
            }
            else {
                String a = compileExpression(expr);
                this.codeGen.startCondition("", a, true);
            }

            Block block = ifstmt.getBlock();

            for ( int i = 0; i < block.getStatementCount(); i++ ) {
                compileStatement(block.getStatement(i));
            }

            this.codeGen.endCondition();

            if ( ifstmt.getElseBlock() != null ) {
                if ( expr instanceof InfixExpression ) {
                    InfixExpression tmp = (InfixExpression) expr;
                    String a = compileExpression(tmp.getLHS());
                    String b = compileExpression(tmp.getRHS());
                    this.codeGen.startCondition(tmp.getOP(), a, false);
                }
                else {
                    String a = compileExpression(expr);
                    this.codeGen.startCondition("", a, false);
                }

                Block block2 = ifstmt.getElseBlock();

                for ( int i = 0; i < block2.getStatementCount(); i++ ) {
                    compileStatement(block2.getStatement(i));
                }

                this.codeGen.endCondition();
            }
        }
        else if ( stmt instanceof WhileStatement ) {
            WhileStatement whilestmt = (WhileStatement) stmt;
            Expression expr = whilestmt.getCondition();

            short ip = this.codeGen.getIP();

            if ( expr instanceof InfixExpression ) {
                InfixExpression tmp = (InfixExpression) expr;
                String a = compileExpression(tmp.getLHS());
                String b = compileExpression(tmp.getRHS());
                this.codeGen.startCondition(tmp.getOP(), a, true);
            }
            else {
                String a = compileExpression(expr);
                this.codeGen.startCondition("", a, true);
            }

            Block block = whilestmt.getBlock();

            for ( int i = 0; i < block.getStatementCount(); i++ ) {
                compileStatement(block.getStatement(i));
            }

            this.codeGen.goTo((short) (ip - (this.codeGen.getIP() + 3)));
            this.codeGen.endCondition();
        }
        else if ( stmt instanceof ForStatement ) {
            debug(1, "Compiling a for loop.");
            ForStatement forstmt = (ForStatement) stmt;

            compileStatement(forstmt.getInitialStatement());

            Expression expr = forstmt.getCondition();
            short ip = this.codeGen.getIP();

            if ( expr instanceof InfixExpression ) {
                InfixExpression tmp = (InfixExpression) expr;
                String a = compileExpression(tmp.getLHS());
                String b = compileExpression(tmp.getRHS());
                this.codeGen.startCondition(tmp.getOP(), a, true);
            }
            else {
                String a = compileExpression(expr);
                this.codeGen.startCondition("", a, true);
            }

            Block block = forstmt.getBlock();

            for ( int i = 0; i < block.getStatementCount(); i++ ) {
                compileStatement(block.getStatement(i));
            }

            debug(1, "Compiling each statement: " + forstmt.getEachStatement());
            compileStatement(forstmt.getEachStatement());
            this.codeGen.goTo((short) (ip - (this.codeGen.getIP() + 3)));
            this.codeGen.endCondition();
        }
        else if ( stmt instanceof JImportStatement ) {
            JImportStatement jImp = (JImportStatement) stmt;

            this.codeGen.loadJavaClass(jImp.getObject().replace("/", "."));

            this.jImports.put(jImp.getNewName(), jImp);
        }
    }

    public void compileAll() {
        Statement stmt = parser.nextStatement();

        while ( stmt != null ) {
            if ( parser.hasError() ) {
                return;
            }

            compileStatement(stmt);
            stmt = parser.nextStatement();
        }
    }

    public void save() {
        this.codeGen.end();
    }

    public void close() {
        this.lexer.close();
    }
}
