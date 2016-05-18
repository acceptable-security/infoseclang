package infosec.AST;

import infosec.AST.Statement.*;
import java.util.ArrayList;

public class Block {
    private ArrayList<Statement> statements;

    public Block() {
        this.statements = new ArrayList<Statement>();
    }

    public Block(Statement stmt) {
        this.statements = new ArrayList<Statement>();
        this.statements.add(stmt);
    }

    public void addStatement(Statement stmt) {
        this.statements.add(stmt);
    }

    public ArrayList<Statement> getStatements() {
        return statements;
    }

    public int getStatementCount() {
        return statements.size();
    }

    public Statement getStatement(int statement) {
        return statements.get(statement);
    }

    public String toString() {
        String out = "{\n";

        for ( int i = 0; i < this.statements.size(); i++ ) {
            out += this.statements.get(i).toString();
        }

        return out + "}\n";
    }
}
