package infosec.lexer.token;

public class StringToken extends Token {
    private String value;

    public StringToken(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public String type() {
        return "String";
    }
}
