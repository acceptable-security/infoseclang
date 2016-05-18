package infosec.AST.Statement;

import infosec.AST.*;
import java.util.ArrayList;

public class FunctionDecStatement extends Statement {
    public class FunctionArg {
        private String name;
        private Type type;
        private int arrayDepth = 0;

        public FunctionArg(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }

        public String toString() {
            return getName() + " : " + getType();
        }
    }

    private String name;
    private Type retType;
    private ArrayList<FunctionArg> args;
    private Block block;

    public FunctionDecStatement(String name) {
        this.name = name;
        this.retType = null;
        this.args = new ArrayList<FunctionArg>();
    }

    public FunctionDecStatement(String name, Type retType) {
        this.retType = retType;
        this.name = name;
        this.args = new ArrayList<FunctionArg>();
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void addArg(String name, Type type) {
        this.args.add(new FunctionArg(name, type));
    }

    public void setRetType(Type retType) {
        this.retType = retType;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return retType;
    }

    public ArrayList<FunctionArg> getArgs() {
        return args;
    }

    public int getArgCount() {
        return args.size();
    }

    public FunctionArg getArg(int i) {
        return args.get(i);
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
