package infosec.AST.Expression;

public class FloatExpression extends Expression {
    private float number;

    public FloatExpression(float number) {
        this.number = number;
    }

    public float getValue() {
        return number;
    }

    public String toString() {
        return number + "";
    }
}
