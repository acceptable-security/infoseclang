package infosec.AST.Statement;

import infosec.AST.Expression.*;
import java.util.Arrays;

public class ExpressionStatement extends Statement {
    private String[] var_dec = new String[] { "=", "+=", "-=", "*=", "/=", "&=", "^="};
    private Expression exp;

    private boolean isVarDec(String op) {
        System.out.println("Testing operator: " + op);
        for ( int i = 0; i < var_dec.length; i++ ) {
            if ( var_dec[i].equals(op) ) {
                return true;
            }
        }

        return false;
    }

    public ExpressionStatement(Expression exp) {
        this.exp = exp;
    }

    public String getType() {
        if ( exp instanceof FunctionCallExpression || exp instanceof MethodCallExpression ) {
            return "fncall";
        }
        else if (( exp instanceof InfixExpression && isVarDec(((InfixExpression) exp).getOP())) || exp instanceof PrefixExpression || exp instanceof SuffixExpression ) {
            return "varset";
        }
        else {
            return "return";
        }
    }

    public Expression getExpression() {
        return exp;
    }

    public String toString() {
        return exp.toString() + ";";
    }
}
