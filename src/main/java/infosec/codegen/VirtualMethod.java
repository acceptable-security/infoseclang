package infosec.codegen;

import infosec.codegen.classfile.*;
import infosec.codegen.classfile.attributes.*;
import infosec.codegen.classfile.constants.*;
import java.util.ArrayList;

public class VirtualMethod {
    private VirtualBlock block;
    private ArrayList<VirtualField> args;
    private String descriptor;
    private String name;
    private int ret_arrayDepth;
    private String ret_type;
    private boolean isPublic = true;
    private boolean isStatic = false;

    public VirtualMethod(String name, String ret_type) {
        this.block = new VirtualBlock((short) 0);
        this.args = new ArrayList<VirtualField>();
        this.descriptor = "";
        this.name = name;
        this.ret_type = ret_type;
        this.ret_arrayDepth = 0;
    }

    public VirtualMethod(String name, String ret_type, int ret_arrayDepth) {
        this.block = new VirtualBlock((short) 0);
        this.args = new ArrayList<VirtualField>();
        this.descriptor = "";
        this.name = name;
        this.ret_type = ret_type;
        this.ret_arrayDepth = ret_arrayDepth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addArg(String name, String type) {
        this.block.nextVariable();
        this.args.add(new VirtualField(name, type));
    }

    public void addArg(String name, String type, int arrayDepth) {
        this.block.nextVariable();
        this.args.add(new VirtualField(name, type, arrayDepth));
    }

    public void setReturnType(String ret_type) {
        this.ret_type = ret_type;
    }

    public void setReturnArrayDepth(int ret_arrayDepth) {
        this.ret_arrayDepth = ret_arrayDepth;
    }

    public void addOperation(OPCode op) {
        this.block.addOperation(op);
    }

    public void addOperation(OPCode op, byte arg) {
        this.block.addOperation(op, arg);
    }

    public void addOperation(OPCode op, short arg) {
        this.block.addOperation(op, arg);
    }

    public void addOperation(OPCode op, byte arg, byte arg2) {
        this.block.addOperation(op, arg, arg2);
    }

    public void addRealOperation(OPCode op) {
        this.block.addRealOperation(op);
    }

    public void addRealOperation(OPCode op, byte arg) {
        this.block.addRealOperation(op, arg);
    }

    public void addRealOperation(OPCode op, short arg) {
        this.block.addRealOperation(op, arg);
    }

    public void addRealOperation(OPCode op, byte arg, byte arg2) {
        this.block.addRealOperation(op, arg, arg2);
    }

    public void startBlock() {
        this.block.startBlock();
    }

    public int getInternalBlockSize() {
        return this.block.getInternalBlockSize();
    }

    public void endBlock() {
        this.block.endBlock();
    }

    public void setPublic(boolean val) {
        this.isPublic = val;
    }

    public void setStatic(boolean val) {
        this.isStatic = val;
    }

    private String getReturnType() {
        if ( ret_type.equals("void") ) {
            return "V";
        }

        String[] names = new String[]{
            "byte",
            "char",
            "double",
            "float",
            "int",
            "long",
            "short",
            "bool"
        };

        String[] types = new String[] {
            "B",
            "C",
            "D",
            "F",
            "I",
            "J",
            "S",
            "Z"
        };

        String out = "";

        for ( int i = 0; i < ret_arrayDepth; i++ ) {
            out += "[";
        }

        for ( int i = 0; i < names.length; i++ ) {
            if ( this.ret_type.equals(names[i]) ) {
                out += types[i];
                return out;
            }
        }

        out += "L" + ret_type + ";";

        return out;
    }

    public String getDescriptor() {
        String out = "(";

        for ( int i = 0; i < this.args.size(); i++ ) {
            out += this.args.get(i).toString();
        }

        out += ")" + getReturnType();

        return out;
    }

    public short getIP() {
        return this.block.getIP();
    }

    public byte[] byteCode() {
        return this.block.byteCode();
    }

    public short nextVariable() {
        return this.block.nextVariable();
    }

    public short getVariableCount() {
        return this.block.getVariableCount();
    }

    public void compileTo(CodeEmitter codegen) {
        MethodInfo method = new MethodInfo(codegen.utf8(name), codegen.utf8(getDescriptor()));
        CodeAttribute code = new CodeAttribute(codegen.utf8("Code"), (short) 200, this.block.getVariableCount()); // TODO - Pick less arbitrary numbers
        code.setCode(byteCode());
        method.addAttribute(code);

        if ( isPublic ) {
            method.addFlag(AccessFlags.ACC_PUBLIC);
        }
        else  {
            method.removeFlag(AccessFlags.ACC_PUBLIC);
        }

        if ( isStatic ) {
            method.addFlag(AccessFlags.ACC_STATIC);
        }
        else  {
            method.removeFlag(AccessFlags.ACC_STATIC);
        }

        codegen.method(method);
    }
}
