package infosec.codegen;

import infosec.codegen.classfile.*;
import infosec.codegen.classfile.attributes.*;
import infosec.codegen.classfile.constants.*;
import java.util.ArrayList;

public class VirtualMethod {
    private ArrayList<Byte> byteCode;
    private ArrayList<VirtualField> args;
    private String descriptor;
    private String name;
    private int ret_arrayDepth;
    private String ret_type;
    private boolean isPublic = true;
    private boolean isStatic = false;

    public VirtualMethod(String name, String ret_type) {
        this.byteCode = new ArrayList<Byte>();
        this.args = new ArrayList<VirtualField>();
        this.descriptor = "";
        this.name = name;
        this.ret_type = ret_type;
        this.ret_arrayDepth = 0;
    }

    public VirtualMethod(String name, String ret_type, int ret_arrayDepth) {
        this.byteCode = new ArrayList<Byte>();
        this.args = new ArrayList<VirtualField>();
        this.descriptor = "";
        this.name = name;
        this.ret_type = ret_type;
        this.ret_arrayDepth = ret_arrayDepth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addArg(String name, String type) {
        this.args.add(new VirtualField(name, type));
    }

    public void addArg(String name, String type, int arrayDepth) {
        this.args.add(new VirtualField(name, type, arrayDepth));
    }

    public void setReturnType(String ret_type) {
        this.ret_type = ret_type;
    }

    public void setReturnArrayDepth(int ret_arrayDepth) {
        this.ret_arrayDepth = ret_arrayDepth;
    }

    public void addOperation(OPCode op) {
        this.byteCode.add((byte) op.getOP());
    }

    public void addOperation(OPCode op, byte arg) {
        this.byteCode.add((byte) op.getOP());
        this.byteCode.add(arg);
    }

    public void addOperation(OPCode op, short arg) {
        this.byteCode.add((byte) op.getOP());
        this.byteCode.add((byte) ((arg >> 8) & 0xFF));
        this.byteCode.add((byte) (arg & 0xFF));
    }

    public void addOperation(OPCode op, byte arg, byte arg2) {
        this.byteCode.add((byte) op.getOP());
        this.byteCode.add(arg);
        this.byteCode.add(arg2);
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

    public byte[] byteCode() {
        byte[] bytes = new byte[byteCode.size()];

        for ( int i = 0; i < byteCode.size(); i++ ) {
            bytes[i] = byteCode.get(i).byteValue();
        }

        return bytes;
    }

    public void compileTo(CodeGen codegen) {
        MethodInfo method = new MethodInfo(codegen.storeString(name), codegen.storeString(getDescriptor()));
        CodeAttribute code = new CodeAttribute(codegen.storeString("Code"), (short) 200, (short) 7); // TODO - Pick less arbitrary numbers
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

        codegen.storeMethod(method);
    }
}
