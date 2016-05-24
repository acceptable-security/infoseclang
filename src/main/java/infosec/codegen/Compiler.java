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
    private int debugLevel = 0;

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

            compileExpression(dec.getValue());

            this.codeGen.storeLastVariable(dec.getName(), dec.getType().getBasicType());
        }
        else if ( stmt instanceof ExpressionStatement ) {
            debug(1, "Compiling a expression statement.");

            ExpressionStatement exprstmt = (ExpressionStatement) stmt;

            if ( exprstmt.getType().equals("varset") ) {
                debug(1, "Variable set expression detected...");

                InfixExpression expr = (InfixExpression) exprstmt.getExpression();
                Expression _lhs = expr.getLHS();
                Expression _rhs = expr.getRHS();

                if ( !(_lhs instanceof VariableExpression ) ) {
                    System.out.println("Failed to find a left hand side for a variable set expression.");
                    return;
                }

                String lhs = ((VariableExpression) _lhs).getName();
                String type = compileExpression(_rhs);
                this.codeGen.storeLastVariable(lhs, type);
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
