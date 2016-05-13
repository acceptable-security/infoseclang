package infosec.AST.Statement;

import infosec.AST.Expression.*;
import java.util.Arrays;

public class ExpressionStatement extends Statement {
    private String[] var_dec = new String[] {"=", "+=", "-=", "*=", "/=", "&=", "^="};
    private Expression exp;

    private boolean isVarDec(String op) {
        return Arrays.asList(var_dec).indexOf(op) > -1;
    }

    public ExpressionStatement(Expression exp) {
        this.exp = exp;
    }

    public String getType() {
        if ( exp instanceof FunctionCallExpression ) {
            return "fncall";
        }
        else if ( exp instanceof InfixExpression && isVarDec(((InfixExpression) exp).getOP()) ) {
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
