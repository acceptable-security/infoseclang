package infosec.AST.Statement;

import infosec.AST.Expression.*;
import infosec.AST.Block;

public class WhileStatement extends Statement {
    private Expression condition;
    private Block block;

    public WhileStatement(Expression condition) {
        this.condition = condition;
    }

    public WhileStatement(Expression condition, Block block) {
        this.condition = condition;
        this.block = block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Expression getCondition() {
        return this.condition;
    }

    public Block getBlock() {
        return this.block;
    }

    public String toString() {
        return "while " + this.condition + " " + block;
    }
}
