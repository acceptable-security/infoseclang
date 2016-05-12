package infosec.lexer.token;

public class NumberToken extends Token {
    private int value;

    public NumberToken(int value) {
        this.value = value;
    }

    public String value() {
        return this.value + "";
    }

    public int intValue() {
        return this.value;
    }

    public String type() {
        return "Number";
    }
}
