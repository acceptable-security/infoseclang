package infosec;

import infosec.lexer.*;
import infosec.parser.*;
import infosec.AST.Statement.*;
import infosec.codegen.*;
import infosec.codegen.Compiler;
import infosec.codegen.classfile.*;


public class InfoSec {
    public static void main(String[] argv) {
        Compiler c = new Compiler(argv[0]);
        c.compileAll();
        c.save();
        c.close();
        // CodeGen cg = new CodeGen("test");
        //     // fun main(argv: test[]) : void
        //     cg.startFunction("main", "void");
        //     cg.addFunctionArgument("argv", "java/lang/String", 1);
        //         // var test : str = "Hello World";
        //         cg.storeStringVariable("test", "Hello World");
        //
        //         // test = "Hello!!!!";
        //         cg.storeStringVariable("test", "Hello!!!!");
        //
        //         // System.out.println(test);
        //         cg.startMethodCall("java/lang/System", "out", "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        //             cg.pushVariable("test");
        //         cg.endMethodCall();
        //
        //     // return;
        //     cg.endFunction();
        //
        // cg.end();


        // Lexer lex = new Lexer(argv[0]);
        // Parser parser = new Parser(lex);
        //
        // Statement stmt = parser.nextStatement();
        //
        // while ( stmt != null ) {
        //     System.out.println(stmt);
        //     stmt = parser.nextStatement();
        // }
        //
        // lex.close();
    }
}
