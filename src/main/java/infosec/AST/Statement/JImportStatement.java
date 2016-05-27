package infosec.AST.Statement;

import infosec.AST.*;
import infosec.AST.Expression.*;

public class JImportStatement extends Statement {
    private String object;
    private String field;
    private String fieldType;
    private String method;
    private String methodType;
    private String newName;

    public JImportStatement(String object, String field, String fieldType, String method, String methodType, String newName) {
        this.object = object;
        this.field = field;
        this.fieldType = fieldType;
        this.method = method;
        this.methodType = methodType;
        this.newName = newName;
    }

    public String getObject() {
        return this.object;
    }

    public String getField() {
        return this.field;
    }

    public String getFieldType() {
        return this.fieldType;
    }

    public String getMethod() {
        return this.method;
    }

    public String getMethodType() {
        return this.methodType;
    }

    public String getNewName() {
        return this.newName;
    }

    public String toString() {
        return "@jimport(" + this.object + ", " + this.field + ", " + this.fieldType + ", " + this.method + ", " + this.methodType + ") as " + this.newName +"\n";
    }
}
