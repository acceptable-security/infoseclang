package infosec.AST.Expression;

public class ArrayDereferenceExpression extends Expression {
    private Expression lhs;
    private Expression inner;

    public ArrayDereferenceExpression(Expression lhs, Expression inner) {
        this.lhs = lhs;
        this.inner = inner;
    }

    public Expression getLHS() {
        return lhs;
    }

    public Expression getInner() {
        return inner;
    }

    public String toString() {
        return lhs + "[" + inner + "]";
    }
}
