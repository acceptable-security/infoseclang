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
    }

    public void debug(int level, String msg) {
        if ( debugLevel >= level ) {
            System.out.println("[DEBUG] " + msg);
        }
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
