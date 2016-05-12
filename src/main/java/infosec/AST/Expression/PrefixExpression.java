package infosec.AST.Expression;

public class PrefixExpression extends Expression {
    private String op;
    private Expression rhs;

    public PrefixExpression(String op, Expression rhs) {
        this.op = op;
        this.rhs = rhs;
    }

    public String getOP() {
        return op;
    }

    public Expression getRHS() {
        return rhs;
    }

    public String toString() {
        return op + rhs.toString();
    }
}
