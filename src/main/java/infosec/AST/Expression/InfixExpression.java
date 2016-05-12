package infosec.AST.Expression;

public class InfixExpression extends Expression {
    private String op;
    private Expression lhs;
    private Expression rhs;

    public InfixExpression(Expression lhs, String op, Expression rhs) {
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
    }

    public Expression getLHS() {
        return lhs;
    }

    public String getOP() {
        return op;
    }

    public Expression getRHS() {
        return rhs;
    }

    public String toString() {
        return "(" + lhs.toString() + op + rhs.toString() + ")";
    }

}
