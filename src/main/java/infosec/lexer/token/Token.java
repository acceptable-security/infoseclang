package infosec.lexer.token;

public abstract class Token {
    public abstract String value();

    public boolean equalsTo(Token other) {
        return other.value().equals(value()) && other.type().equals(type());
    }

    public abstract String type();

    public String toString() {
        return type() + "<" + value() + ">";
    }
}
