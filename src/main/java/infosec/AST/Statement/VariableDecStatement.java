package infosec.AST.Statement;

import infosec.AST.*;
import infosec.AST.Expression.*;

public class VariableDecStatement extends Statement {
    private String name;
    private Type type;
    private Expression value;

    public VariableDecStatement(String name, Type type, Expression value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public Expression getValue() {
        return this.value;
    }

    public String toString() {
        return "var " + this.name + " : " + this.type + " = " + this.value + ";\n";
    }
}
