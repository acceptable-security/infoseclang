package infosec;

import infosec.lexer.*;
import infosec.parser.*;
import infosec.AST.Statement.*;

public class InfoSec {
    public static void main(String[] argv) {
        Lexer lex = new Lexer(argv[0]);
        Parser parser = new Parser(lex);

        Statement stmt = parser.nextStatement();

        while ( stmt != null ) {
            System.out.println(stmt);
            stmt = parser.nextStatement();
        }

        lex.close();
    }
}
