package infosec.lexer.token;

public abstract class Token {
    public abstract String value();
    private int lineNumber;

    public boolean equalsTo(Token other) {
        return other.value().equals(value()) && other.type().equals(type());
    }

    public abstract String type();

    public String toString() {
        return type() + "<" + value() + ">";
    }

    public void setLineNumber(int lintNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }
}
