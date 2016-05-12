package infosec.lexer.token;

public class NameToken extends Token {
    private String value;

    public NameToken(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public String type() {
        return "Name";
    }
}
