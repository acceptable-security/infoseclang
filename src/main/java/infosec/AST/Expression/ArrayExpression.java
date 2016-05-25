package infosec.AST.Expression;

import java.util.ArrayList;

public class ArrayExpression extends Expression {
    private ArrayList<Expression> lhs;

    public ArrayExpression() {
        this.lhs = new ArrayList<Expression>();
    }

    public void addExpression(Expression exp) {
        this.lhs.add(exp);
    }

    public Expression getExpression(int i) {
         return this.lhs.get(i);
    }

    public int getExpressionCount() {
        return this.lhs.size();
    }

    public String toString() {
        String str = "[";

        for ( int i = 0; i < getExpressionCount(); i++ ) {
            str += getExpression(i);

            if ( i != i - 1 ) {
                str += ", ";
            }
        }

        return str + "]";
    }
}
