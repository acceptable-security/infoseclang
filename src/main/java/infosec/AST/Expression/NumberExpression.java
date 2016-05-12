package infosec.AST.Expression;

public class NumberExpression extends Expression {
    private int number;

    public NumberExpression(int number) {
        this.number = number;
    }

    public int getValue() {
        return number;
    }

    public String toString() {
        return number + "";
    }
}
