package infosec.AST.Expression;

public class StringExpression extends Expression {
    private String str;

    public StringExpression(String str) {
        this.str = str;
    }

    public String getValue() {
        return str;
    }

    public String toString() {
        return "\"" + str + "\"";
    }
}
