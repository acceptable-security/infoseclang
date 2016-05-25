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
    private int debugLevel = 1;

    public Compiler(String filename) {
        Path path = Paths.get(filename);
        String file = path.getFileName().toString();
        String name = file.substring(0, file.lastIndexOf("."));
        this.codeGen = new CodeGen(name);
        this.lexer = new Lexer(filename);
        this.parser = new Parser(this.lexer);

        standardFunctions();
    }

    public void debug(int level, String msg) {
        if ( debugLevel >= level ) {
            System.out.println("[DEBUG] " + msg);
        }
    }

    public void standardFunctions() {
        this.codeGen.startFunction("print", "void");
        this.codeGen.addFunctionArgument("str", "java/lang/String");
            this.codeGen.startMethodCall("java/lang/System", "out", "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
                this.codeGen.pushVariable("str");
            this.codeGen.endMethodCall();
        this.codeGen.endFunction();
    }

    public Type locateType(Expression expr) {
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
        else if ( expr instanceof VariableExpression ) {
            String name = ((VariableExpression) expr).getName();
            return new Type(this.codeGen.getVariableType(name), this.codeGen.getArrayDepth(name));
        }
        else if ( expr instanceof FunctionCallExpression ) {
            String name = ((FunctionCallExpression) expr).getName();
            return new Type(this.codeGen.getFunctionType(name));
        }
        else if ( expr instanceof ArrayDereferenceExpression ) {
            return locateType(((ArrayDereferenceExpression) expr).getLHS());
        }

        return null;
    }

    public String compileExpression(Expression expr) {
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
            this.codeGen.pushVariable(((VariableExpression) expr).getName());
            return this.codeGen.getVariableType(((VariableExpression) expr).getName());
        }
        else if ( expr instanceof FunctionCallExpression ) {
            FunctionCallExpression call = (FunctionCallExpression) expr;

            this.codeGen.startFunctionCall(call.getName(), this.codeGen.getFunctionType(call.getName()));

            for ( int i = 0; i < call.getArgCount(); i++ ) {
                compileExpression(call.getArg(i));
            }

            this.codeGen.endFunctionCall();
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
            String type = compileExpression(pfx.getRHS());

            if ( pfx.getOP().equals("#") ) {
                this.codeGen.pushArrayLength();
                return "int";
            }
            else {
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
                }
                else {
                    return "";
                }
            }

            return type;
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
        return "";
    }

    public void compileStatement(Statement stmt) {
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
                System.out.println("Resolved type to " + type);
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
                compileExpression(exprstmt.getExpression());
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
        else if ( stmt instanceof ForStatement) {
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
    }

    public void compileAll() {
        Statement stmt = parser.nextStatement();

        while ( stmt != null ) {
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
