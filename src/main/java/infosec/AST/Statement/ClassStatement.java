package infosec.AST.Statement;

import infosec.AST.Block;
import infosec.AST.Expression.*;
import java.util.ArrayList;

public class ClassStatement extends Statement {
    private String name;
    private ArrayList<String> inheritors;
    private Block block;

    public ClassStatement(String name) {
        this.name = name;
        this.inheritors = new ArrayList<String>();
        this.block = new Block();
    }

    public void addInheritor(String inheritor) {
        this.inheritors.add(inheritor);
    }

    public String getInheritor(int i) {
        return this.inheritors.get(i);
    }

    public int getInheritorCount() {
        return this.inheritors.size();
    }

    public String getName() {
        return this.name;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }

    public String toString() {
        String out = "class " + this.name;

        if ( this.inheritors.size() > 0 ) {
            out += " : ";

            for ( int i = 0; i < inheritors.size(); i++ ) {
                out += inheritors.get(i);

                if ( i < i - 1 ) {
                    out += ", ";
                }
            }
        }

        out += block.toString();

        return out;
    }
}
