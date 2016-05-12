package infosec.AST.Expression;

public class SuffixExpression extends Expression {
    private String op;
    private Expression lhs;

    public SuffixExpression(String op, Expression lhs) {
        this.op = op;
        this.lhs = lhs;
    }

    public String getOP() {
        return op;
    }

    public Expression getLHS() {
        return lhs;
    }

    public String toString() {
        return lhs.toString() + op;
    }
}
