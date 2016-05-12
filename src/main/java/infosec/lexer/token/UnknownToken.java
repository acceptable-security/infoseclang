package infosec.lexer.token;

public class UnknownToken extends Token {
    private String value;

    public UnknownToken(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public String type() {
        return "Name";
    }
}
