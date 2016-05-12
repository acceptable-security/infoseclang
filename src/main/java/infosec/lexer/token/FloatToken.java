package infosec.lexer.token;

public class FloatToken extends Token {
    private float value;

    public FloatToken(float value) {
        this.value = value;
    }

    public String value() {
        return this.value + "";
    }

    public float floatValue() {
        return value;
    }

    public String type() {
        return "Float";
    }
}
