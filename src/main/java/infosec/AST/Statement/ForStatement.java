package infosec.AST.Statement;

import infosec.AST.Expression.*;
import infosec.AST.Block;

public class ForStatement extends Statement {
    private Statement initial;
    private Expression condition;
    private Statement each;
    private Block block;

    public ForStatement(Statement initial, Expression condition, Statement each) {
        this.initial = initial;
        this.condition = condition;
        this.each = each;
    }

    public ForStatement(Statement initial, Expression condition, Statement each, Block block) {
        this.initial = initial;
        this.condition = condition;
        this.each = each;
        this.block = block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Statement getInitialStatement() {
        return initial;
    }

    public Statement getEachStatement() {
        return each;
    }

    public Expression getCondition() {
        return condition;
    }

    public Block getBlock() {
        return block;
    }

    public String toString() {
        return "for " + this.initial + "; " + this.condition + "; " + this.each + " " + block;
    }
}
