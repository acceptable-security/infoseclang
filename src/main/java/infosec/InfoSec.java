package infosec;

import infosec.lexer.*;
import infosec.parser.*;
import infosec.AST.Statement.*;
import infosec.codegen.*;
import infosec.codegen.Compiler;
import infosec.codegen.classfile.*;


public class InfoSec {
    public static void main(String[] argv) {
        if ( argv.length == 0 ) {
            System.out.println("Usage: infosec filename");
            return;
        }

        System.out.println("Compiling " + argv[0]);
        Compiler c = new Compiler(argv[0]);
        c.compileAll();
        c.save();
        c.close();
    }
}
