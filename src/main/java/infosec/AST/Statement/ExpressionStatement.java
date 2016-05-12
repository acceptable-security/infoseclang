package infosec.AST.Statement;

import infosec.AST.Expression.*;

public class ExpressionStatement extends Statement {
    private Expression exp;

    public ExpressionStatement(Expression exp) {
        this.exp = exp;
    }

    public String getType() {
        if ( exp instanceof FunctionCallExpression ) {
            return "fncall";
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
