package infosec.AST.Expression;

import java.util.ArrayList;

public class FunctionCallExpression extends Expression {
    private ArrayList<Expression> args;
    private String name;

    public FunctionCallExpression(String name) {
        this.name = name;
        this.args = new ArrayList<Expression>();
    }

    public void addArg(Expression arg) {
        this.args.add(arg);
    }

    public ArrayList<Expression> getArgs() {
        return this.args;
    }

    public String toString() {
        String out = name + "(";

        for ( int i = 0; i < this.args.size(); i++ ) {
            out += this.args.get(i).toString() + (i == this.args.size()  - 1 ? "" : ", ");
        }

        return out + ")";
    }
}
