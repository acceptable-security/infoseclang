package infosec.AST.Expression;

import java.util.ArrayList;

public class MethodCallExpression extends Expression {
    private ArrayList<Expression> args;
    private Expression lhs;
    private String name;

    public MethodCallExpression(Expression lhs, String name) {
        this.lhs = lhs;
        this.name = name;
        this.args = new ArrayList<Expression>();
    }

    public void addArg(Expression arg) {
        this.args.add(arg);
    }

    public ArrayList<Expression> getArgs() {
        return this.args;
    }

    public int getArgCount() {
        return this.args.size();
    }

    public Expression getArg(int i) {
        return this.args.get(i);
    }

    public String getName() {
        return name;
    }

    public Expression getLHS() {
        return lhs;
    }

    public String toString() {
        String out = lhs + "." + name + "(";

        for ( int i = 0; i < this.args.size(); i++ ) {
            out += this.args.get(i).toString() + (i == this.args.size()  - 1 ? "" : ", ");
        }

        return out + ")";
    }
}
