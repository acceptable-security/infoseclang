package infosec.AST.Statement;

import infosec.AST.Expression.*;
import infosec.AST.Block;

public class RangeStatement extends Statement {
    private String variable;
    private int start;
    private int end;
    private Block block;

    public RangeStatement(String variable, int start, int end) {
        this.variable = variable;
        this.start = start;
        this.end = end;
    }

    public RangeStatement(String variable, int start, int end, Block block) {
        this.variable = variable;
        this.start = start;
        this.end = end;
        this.block = block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public String getVariable() {
        return variable;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String toString() {
        return "range " + this.variable + " from " + this.start + " to " + this.end + " " + block;
    }
}
