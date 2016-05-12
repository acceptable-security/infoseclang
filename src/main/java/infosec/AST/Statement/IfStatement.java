package infosec.AST.Statement;

import infosec.AST.Expression.*;
import infosec.AST.Block;

public class IfStatement extends Statement {
    private Expression condition;
    private Block ifBlock;
    private Block elseBlock;

    public IfStatement(Expression condition) {
        this.condition = condition;
    }

    public IfStatement(Expression condition, Block block) {
        this.condition = condition;
        this.ifBlock = block;
    }

    public IfStatement(Expression condition, Block block, Block elseBlock) {
        this.condition = condition;
        this.ifBlock = block;
        this.elseBlock = elseBlock;
    }

    public void setBlock(Block block) {
        this.ifBlock = block;
    }

    public void setElseBlock(Block block) {
        this.elseBlock = block;
    }

    public Block getBlock() {
        return this.ifBlock;
    }

    public Block getElseBlock() {
        return this.elseBlock;
    }

    public String toString() {
        String out = "if " + this.condition + " " + ifBlock;

        if ( elseBlock != null ) {
            out += "else " + elseBlock;
        }

        return out;
    }
}
