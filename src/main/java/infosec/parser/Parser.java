package infosec.parser;

import infosec.lexer.*;
import infosec.lexer.token.*;
import infosec.AST.*;
import infosec.AST.Expression.*;
import infosec.AST.Statement.*;

public class Parser {
    private Lexer lexer;

    public String[][] op_pres = new String[][] {
        new String[] {"<", ">", ">=", "<=", "==", "!=", "||", "&&"},
        new String[] {"+", "-"},
        new String[] {"*", "%", "/"},
        new String[] {"<<", ">>", "&", "^", "|"},
        new String[] {"=", "+=", "-=", "*=", "/=", "&=", "^="}
    };

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public int getPresidence(String op) {
        for ( int i = 0; i < op_pres.length; i++ ) {
            for ( int j = 0; j < op_pres[i].length; j++ ) {
                if ( op_pres[i][j].equals(op) ) {
                    return i;
                }
            }
        }

        return -1;
    }

    public Expression nextExpression(int pres) {
        Token next = lexer.current();

        if ( next == null ) {
            return null;
        }

        Expression lhs = null;

        if ( matchSpecial("(") ) {
            lhs = nextExpression(0);

            if ( !expectSpecial(")") ) {
                return null;
            }
        }

        if ( next.type() == "Number" ) {
            lexer.next();
            lhs = new NumberExpression(((NumberToken) next).intValue());
        }
        else if ( next.type() == "Float" ) {
            lexer.next();
            lhs = new FloatExpression(((FloatToken) next).floatValue());
        }
        else if ( next.type() == "Name" ) {
            lexer.next();

            if ( matchSpecial("(") ) {
                String name = ((NameToken) next).value();
                FunctionCallExpression exp = new FunctionCallExpression(name);

                while ( (lexer.match("Special", ")") == null) && lexer.current() != null ) {
                    exp.addArg(nextExpression(0));

                    if ( lexer.match("Special", ",") == null && lexer.match("Special", ")") == null ) {
                        System.out.println("Expected , or ), got " + lexer.current());
                        return null;
                    }

                    if ( lexer.match("Special", ")") != null ) {
                        break;
                    }

                    lexer.next();
                }

                lhs = (Expression) exp;

                if ( !expectSpecial(")") ) {
                    return null;
                }
            }
            else {
                lhs = new VariableExpression(((NameToken) next).value());
            }
        }
        else if ( next.type() == "String" ) {
            lexer.next();
            lhs = new StringExpression(((StringToken) next).value());
        }

        while ( lexer.match("Special") != null ) {
            String op = ((SpecialToken) lexer.current()).value();
            int c_pres = getPresidence(op);

            if ( c_pres != -1 ) {
                lexer.next();

                Expression rhs = nextExpression(c_pres);

                if ( rhs instanceof InfixExpression ) {
                    InfixExpression a = (InfixExpression) rhs;

                    if ( c_pres > getPresidence(a.getOP()) ) {
                        lhs = (Expression) new InfixExpression(new InfixExpression(lhs, op, a.getLHS()), a.getOP(), a.getRHS());
                    }
                    else {
                        lhs = (Expression) new InfixExpression(lhs, op, rhs);
                    }
                }
                else {
                    lhs = (Expression) new InfixExpression(lhs, op, rhs);
                }
            }
            else {
                break;
            }
        }

        return lhs;
    }

    public String readName() {
        NameToken tkn = (NameToken) lexer.match("Name");

        if ( tkn == null ) {
            return "";
        }

        lexer.next();

        return tkn.value();
    }

    public Block readBlock() {
        if ( lexer.match("Special", "{") == null ) {
            return new Block(nextStatement());
        }

        lexer.next();

        Block block = new Block();
        Statement stmt = nextStatement();

        while ( stmt != null ) {
            block.addStatement(stmt);
            stmt = nextStatement();
        }

        if ( !expectSpecial("}") ) {
            return null;
        }

        return block;
    }

    public boolean matchName(String token) {
        if ( lexer.match("Name", token) == null ) {
            return false;
        }

        lexer.next();

        return true;
    }

    public boolean matchSpecial(String token) {
        if ( lexer.match("Special", token) == null ) {
            return false;
        }

        lexer.next();

        return true;
    }

    public boolean expectName(String token) {
        if ( lexer.match("Name", token) == null ) {
            System.out.println("Expected a Name<" + token + "> but found " + lexer.current());
            return false;
        }

        lexer.next();

        return true;
    }

    public boolean expectSpecial(String token) {
        if ( lexer.match("Special", token) == null ) {
            System.out.println("Expected a " + token + " but found " + lexer.current());
            return false;
        }

        lexer.next();

        return true;
    }

    public Statement nextStatement() {
        Token next = lexer.current();

        if ( next instanceof NameToken ) {
            NameToken let = (NameToken) next;

            if ( matchName("var") ) {
                String name = readName();

                if ( name.equals("") ) {
                    System.out.println("var must be followed by a name.");
                    return null;
                }

                if ( !expectSpecial(":") ) {
                    return null;
                }

                String type = readName();

                if ( type.equals("") ) {
                    System.out.println("var must be followed by a type.");
                    return null;
                }

                Expression value = null;

                if ( lexer.match("Special", "=") != null ) {
                    lexer.next();
                    value = nextExpression(0);

                    if ( value == null ) {
                        System.out.println("Variable declaration equality must be followed by an expression.");
                        return null;
                    }
                }

                if ( !expectSpecial(";") ) {
                    return null;
                }

                return new VariableDecStatement(name, type, value);
            }
            else if ( matchName("if") ) {
                Expression condition = nextExpression(0);
                Block block = readBlock();
                Block elseBlock = null;

                if ( lexer.match("Name", "else") != null ) {
                    lexer.next();
                    elseBlock = readBlock();
                }

                return new IfStatement(condition, block, elseBlock);
            }
            else if ( matchName("for") ) {
                Expression condition1 = nextExpression(0);

                if ( !expectSpecial(";") ) {
                    return null;
                }

                Expression condition2 = nextExpression(0);

                if ( !expectSpecial(";") ) {
                    return null;
                }

                Expression condition3 = nextExpression(0);
                Block block = readBlock();

                return new ForStatement(condition1, condition2, condition3, block);
            }
            else if ( matchName("while") ) {
                Expression condition = nextExpression(0);
                Block block = readBlock();

                return new WhileStatement(condition, block);
            }
            else if ( matchName("range") ) {
                String variable = readName();

                if ( variable.equals("") ) {
                    System.out.println("Expected a variable, but got a " + lexer.current());
                    return null;
                }

                if ( !expectName("from") ) {
                    return null;
                }

                Token tkn = lexer.next();

                if ( !tkn.type().equals("Number") ) {
                    System.out.println("Expected a number, but got a " + tkn);
                    return null;
                }

                int start = ((NumberToken) tkn).intValue();

                if ( !expectName("to") ) {
                    return null;
                }

                tkn = lexer.next();

                if ( !tkn.type().equals("Number") ) {
                    System.out.println("Expected a number, but got a " + tkn);
                    return null;
                }

                int end = ((NumberToken) tkn).intValue();
                Block block = readBlock();

                return new RangeStatement(variable, start, end, block);
            }
            else if ( matchName("fun") ) {
                String fnName = readName();

                if ( fnName.equals("") ) {
                    System.out.println("Expected a name, got a " + lexer.current());
                    return null;
                }

                if ( !expectSpecial("(") ) {
                    return null;
                }

                FunctionDecStatement dec = new FunctionDecStatement(fnName);

                while ( lexer.current() != null ) {
                    String argName = readName();

                    if ( argName.equals("") )  {
                        break;
                    }

                    if ( !expectSpecial(":") ) {
                        return null;
                    }

                    String argType = readName();

                    if ( argType.equals("") ) {
                        System.out.println("Expected an argument type, got a " + lexer.current());
                        return null;
                    }

                    dec.addArg(argName, argType);

                    if ( !matchSpecial(",") ) {
                        break;
                    }
                }

                if ( !expectSpecial(")") ) {
                    return null;
                }

                if ( matchSpecial(":") ) {
                    String fnRet = readName();

                    if ( fnRet.equals("") ) {
                        System.out.println("Expected a function return type, got a " + fnRet);
                        return null;
                    }

                    dec.setRetType(fnRet);
                }

                dec.setBlock(readBlock());

                return dec;
            }
            else {
                Expression exp = nextExpression(0);

                if ( exp == null ) {
                    return null;
                }

                if ( !expectSpecial(";") ) {
                    return null;
                }

                return new ExpressionStatement(exp);
            }
        }

        return null;
    }
}
