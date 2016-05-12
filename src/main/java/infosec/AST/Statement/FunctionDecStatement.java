package infosec.AST.Statement;

import infosec.AST.*;
import java.util.ArrayList;

public class FunctionDecStatement extends Statement {
    public class FunctionArg {
        private String name;
        private String type;

        public FunctionArg(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String toString() {
            return name + " : " + type;
        }
    }

    private String name;
    private String retType;
    private ArrayList<FunctionArg> args;
    private Block block;

    public FunctionDecStatement(String name) {
        this.name = name;
        this.retType = "";
        this.args = new ArrayList<FunctionArg>();
    }

    public FunctionDecStatement(String name, String retType) {
        this.retType = retType;
        this.name = name;
        this.args = new ArrayList<FunctionArg>();
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void addArg(String name, String type) {
        this.args.add(new FunctionArg(name, type));
    }

    public void setRetType(String retType) {
        this.retType = retType;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return retType;
    }

    public ArrayList<FunctionArg> getArgs() {
        return args;
    }

    public Block getBlock() {
        return block;
    }

    public String toString() {
        String out = "fun " + this.name + "(";

        for ( int i = 0; i < this.args.size(); i++ ) {
            out += this.args.get(i).toString();
        }

        out += ") : " + this.retType + " " + block.toString();
        return out;
    }
}
