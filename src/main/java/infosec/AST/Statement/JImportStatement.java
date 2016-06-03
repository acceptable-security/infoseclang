package infosec.AST.Statement;

import infosec.AST.*;
import infosec.AST.Expression.*;

public class JImportStatement extends Statement {
    private String object;
    private String newName;

    public JImportStatement(String object, String newName) {
        this.object = object;
        this.newName = newName;
    }

    public String getObject() {
        return this.object;
    }

    public String getNewName() {
        return this.newName;
    }

    public String toString() {
        return "@jimport(" + this.object + ") as " + this.newName +"\n";
    }
}
