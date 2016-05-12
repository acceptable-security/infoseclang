package infosec.lexer.token;

public class SpecialToken extends Token {
    private String value;

    public SpecialToken(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public String type() {
        return "Special";
    }
}
