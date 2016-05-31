package infosec.lexer;

import infosec.lexer.token.*;
import java.io.*;

public class Lexer {
    private int debugMode = 0;
    private String tmp = "";
    private String numbers = "1234567890";
    private String whiteSpace = " \t\n";
    private String validName = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
    private int currentLine = 0;
    private String[] special = new String[] {
        "+=",
        "-=",
        "/=",
        "==",
        ">=",
        "<=",
        "!=",
        "&=",
        "&&",
        "||",
        "++",
        "--",
        "//",
        "/*",
        "*/",
        "(",
        ")",
        "[",
        "]",
        "=",
        "+",
        "-",
        "*",
        "%",
        "/",
        "?",
        "<",
        ">",
        ":",
        ",",
        "{",
        "}",
        ";",
        "&",
        "@",
        "^",
        "#",
        "|",
        "."
    };

    private FileReader fileReader;
    private Token current;
    private Token next;

    public Lexer(String path) {
        try {
            this.fileReader = new FileReader(path);
        }
        catch (FileNotFoundException e) {
            this.fileReader = null;
            System.out.println("Failed to open " + path);
        }


        advance();
        advance();
    }

    private String nextChar() {
        if ( fileReader == null ) {
            return "";
        }

        try {
            return Character.toString((char) fileReader.read());
        }
        catch ( IOException e) {
            return "";
        }
    }

    private boolean isWhiteSpace(String chr) {
        return chr.length() == 1 && whiteSpace.indexOf(chr) > -1;
    }

    private boolean isValidName(String chr) {
        return chr.length() == 1 && validName.indexOf(chr) > -1;
    }

    private boolean isNumber(String chr) {
        return chr.length() == 1 && numbers.indexOf(chr) > -1;
    }

    private boolean isSpecial(String chr) {
        for ( int i = 0; i < special.length; i++ ) {
            if ( special[i].indexOf(chr) > -1 ) {
                return true;
            }
        }

        return false;
    }

    private boolean isSpecialToken(String tkn) {
        for ( int i = 0; i < special.length; i++ ) {
            if ( special[i].equals(tkn) ) {
                return true;
            }
        }

        return false;
    }

    private void debug(String msg) {
        if ( debugMode == 1 ) {
            System.out.println("[DEBUG] " + msg);
        }
    }

    private Token readToken() {
        String c = "";

        if ( !tmp.equals("") ) {
            c = tmp;
            tmp = "";
        }
        else {
            c = nextChar();
        }

        while ( isWhiteSpace(c) ) {
            if ( c.equals("\n") ) {
                currentLine += 1;
            }
            c = nextChar();
        }

        if ( isValidName(c) ) {
            String name = c;

            c = nextChar();

            while ( isValidName(c) || isNumber(c) ) {
                name += c;
                c = nextChar();
            }

            tmp = c;

            debug("Name \"" + name + "\"");

            Token tkn = new NameToken(name);
            tkn.setLineNumber(currentLine);
            return tkn;
        }
        else if ( c.equals("\"") ) {
            String str = "";
            String last = "";
            c = nextChar();

            while ( !(c.equals("\"") && !last.equals("\\")) ) {
                str += c;
                last = c;
                c = nextChar();
            }

            debug("String " + str);

            Token tkn = new StringToken(str);
            tkn.setLineNumber(currentLine);
            return tkn;
        }
        else if ( isNumber(c) || c.equals("-") ) {
            String nstr = "";

            if ( c.equals("-") ) {
                nstr += "-";
                c = nextChar();
            }

            while ( isNumber(c) ) {
                nstr += c;
                c = nextChar();
            }

            if ( c.equals(".") ) {
                nstr += ".";

                while ( isNumber(c) ) {
                    nstr += c;
                    c = nextChar();
                }

                tmp = c;
            }
            else {
                tmp = c;
            }

            if ( nstr.equals("-") ) {
                Token tkn = new SpecialToken(nstr);
                tkn.setLineNumber(currentLine);
                return tkn;
            }

            if ( nstr.indexOf(".") > -1 ) {
                debug("Float " + nstr);
                Token tkn = new FloatToken(Float.parseFloat(nstr));
                tkn.setLineNumber(currentLine);
                return tkn;
            }

            debug("Integer " + nstr);
            Token tkn = new NumberToken(Integer.parseInt(nstr));
            tkn.setLineNumber(currentLine);
            return tkn;
        }
        else if ( isSpecial(c) ) {
            String s = c;

            c = nextChar();

            while ( isSpecial(c) && isSpecialToken(s + c)) {
                s += c;
                c = nextChar();
            }

            tmp = c;

            debug("Special " + s);

            for ( int i = 0; i < special.length; i++ ) {
                if ( special[i].equals(s) ) {
                    Token tkn = new SpecialToken(s);
                    tkn.setLineNumber(currentLine);
                    return tkn;
                }
            }
        }

        Token tkn = new UnknownToken(c);
        tkn.setLineNumber(currentLine);
        return tkn;
    }

    private void advance() {
        current = next;
        next = readToken();
    }

    public Token current() {
        return current;
    }

    public Token lookahead() {
        return next;
    }

    public Token next() {
        Token tkn = current;
        advance();
        return tkn;
    }

    public Token match(String type) {
        if ( current == null ) {
            return null;
        }

        if ( current.type().equals(type) ) {
            return current;
        }

        return null;
    }

    public Token match(String type, String value) {
        if ( current == null ) {
            return null;
        }

        if ( current.type().equals(type) && current.value().equals(value) ) {
            return current;
        }

        return null;
    }

    public Token matchAhead(String type) {
        if ( next == null ) {
            return null;
        }

        if ( next.type().equals(type) ) {
            return next;
        }

        return null;
    }

    public Token matchAhead(String type, String value) {
        if ( next == null ) {
            return null;
        }

        if ( next.type().equals(type) && next.value().equals(value) ) {
            return next;
        }

        return null;
    }

    public boolean must(String type, String value) {
        if ( current == null ) {
            return false;
        }

        if ( current.type().equals(type) && current.value().equals(value) ) {
            advance();
            return true;
        }

        System.out.println("Lexer error: " + value + " not found.");
        return false;
    }

    public boolean hasNext() {
        return current != null;
    }

    public void close() {
        try {
            this.fileReader.close();
        }
        catch (IOException e) {
            return;
        }
    }
}
