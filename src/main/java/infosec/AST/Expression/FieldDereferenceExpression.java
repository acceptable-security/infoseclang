package infosec.AST.Expression;

import java.util.ArrayList;

public class FieldDereferenceExpression extends Expression {
    private Expression lhs;
    private String name;

    public FieldDereferenceExpression(Expression lhs, String name) {
        this.lhs = lhs;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Expression getLHS() {
        return lhs;
    }

    public String toString() {
        return lhs + "." + name;
    }
}
