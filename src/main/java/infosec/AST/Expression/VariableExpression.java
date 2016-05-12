package infosec.AST.Expression;

public class VariableExpression extends Expression {
    private String name;

    public VariableExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
