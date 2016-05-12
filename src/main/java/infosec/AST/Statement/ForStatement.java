package infosec.AST.Statement;

import infosec.AST.Expression.*;
import infosec.AST.Block;

public class ForStatement extends Statement {
    private Expression condition1;
    private Expression condition2;
    private Expression condition3;
    private Block block;

    public ForStatement(Expression condition1, Expression condition2, Expression condition3) {
        this.condition1 = condition1;
        this.condition2 = condition2;
        this.condition3 = condition3;
    }

    public ForStatement(Expression condition1, Expression condition2, Expression condition3, Block block) {
        this.condition1 = condition1;
        this.condition2 = condition2;
        this.condition3 = condition3;
        this.block = block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Expression[] getExpressions() {
        return new Expression[] {
            condition1,
            condition2,
            condition3
        };
    }

    public Block getBlock() {
        return block;
    }

    public String toString() {
        return "for " + this.condition1 + "; " + this.condition2 + "; " + this.condition3 + " " + block;
    }
}
