package infosec.codegen;

import infosec.lexer.*;
import infosec.parser.*;
import infosec.AST.Statement.*;
import infosec.codegen.classfile.*;
import java.util.HashMap;
import java.util.Stack;

public class CodeGen {
    private enum Type {
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        CHAR,
        REF
    };

    private CodeEmitter emitter;
    private VirtualMethod method;
    private String name;
    private HashMap<String, Short> variables;
    private HashMap<String, Type> varTypes;
    private HashMap<String, String> functionType;
    private short currentCallField = -1;
    private short currentCallMethod = -1;
    private short conditionBeforeSize;
    private Stack<OPCode> tmpOp;

    public CodeGen(String name) {
        this.name = name;
        this.variables = new HashMap<String, Short>();
        this.varTypes = new HashMap<String, Type>();
        this.functionType = new HashMap<String, String>();

        this.emitter = new CodeEmitter();
        this.emitter.thisClass(name);
        this.emitter.superClass("java/lang/Object");
        this.emitter.setSuper(true);
        this.emitter.setPublic(true);

        this.tmpOp = new Stack<OPCode>();
    }

    public Type typeFromString(String type) {
        if ( type.equals("byte") ) {
            return Type.BYTE;
        }
        else if ( type.equals("short") ) {
            return Type.SHORT;
        }
        else if ( type.equals("int") ) {
            return Type.INT;
        }
        else if ( type.equals("long") ) {
            return Type.LONG;
        }
        else if ( type.equals("float") ) {
            return Type.FLOAT;
        }
        else if ( type.equals("double") ) {
            return Type.DOUBLE;
        }
        else if ( type.equals("char") ) {
            return Type.CHAR;
        }

        return Type.REF;
    }

    public void pushInteger(int val) {
        if ( method == null ) {
            return;
        }

        if ( val < 128 || val > -128 ) {
            method.addOperation(OPCode.OP_bipush, (byte) val);
        }
        else {
            short ind = this.emitter.intVal(val);
            method.addOperation(OPCode.OP_ldc, (byte) ind);
        }
    }

    public void pushFloat(float val) {
        if ( method == null ) {
            return;
        }

        short ind = this.emitter.floatVal(val);
        method.addOperation(OPCode.OP_ldc, (byte) ind);
    }

    public void pushString(String str) {
        if ( method == null ) {
            return;
        }

        short ind = this.emitter.string(str);
        method.addOperation(OPCode.OP_ldc, (byte) ind);
    }

    public void pushVariable(String name) {
        if ( this.method == null ) {
            return;
        }

        if ( !this.variables.containsKey(name) || !this.varTypes.containsKey(name) ) {
            return;
        }

        short local = this.variables.get(name);

        switch ( this.varTypes.get(name) ) {
            case INT:
                if ( local < 4 ) {
                    switch ( local ) {
                        case 0: this.method.addOperation(OPCode.OP_iload_0); break;
                        case 1: this.method.addOperation(OPCode.OP_iload_1); break;
                        case 2: this.method.addOperation(OPCode.OP_iload_2); break;
                        case 3: this.method.addOperation(OPCode.OP_iload_3); break;
                    }
                }
                else {
                    this.method.addOperation(OPCode.OP_iload, (byte) local);
                }
                break;

            case REF:
                if ( local < 4 ) {
                    switch ( local ) {
                        case 0: this.method.addOperation(OPCode.OP_aload_0); break;
                        case 1: this.method.addOperation(OPCode.OP_aload_1); break;
                        case 2: this.method.addOperation(OPCode.OP_aload_2); break;
                        case 3: this.method.addOperation(OPCode.OP_aload_3); break;
                    }
                }
                else {
                    this.method.addOperation(OPCode.OP_aload, (byte) local);
                }
                break;
        }
    }

    public void operation(String op, String _type) {
        Type type = typeFromString(_type);

        if ( op.equals("+") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_iadd); break;
                case LONG: method.addOperation(OPCode.OP_ladd); break;
                case FLOAT: method.addOperation(OPCode.OP_fadd); break;
                case DOUBLE:  method.addOperation(OPCode.OP_dadd); break;
                default: return;
            }
        }
        else if ( op.equals("-") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_isub); break;
                case LONG: method.addOperation(OPCode.OP_lsub); break;
                case FLOAT: method.addOperation(OPCode.OP_fsub); break;
                case DOUBLE:  method.addOperation(OPCode.OP_dsub); break;
                default: return;
            }
        }
        else if ( op.equals("*") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_imul); break;
                case LONG: method.addOperation(OPCode.OP_lmul); break;
                case FLOAT: method.addOperation(OPCode.OP_fmul); break;
                case DOUBLE:  method.addOperation(OPCode.OP_dmul); break;
                default: return;
            }
        }
        else if ( op.equals("/") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_idiv); break;
                case LONG: method.addOperation(OPCode.OP_ldiv); break;
                case FLOAT: method.addOperation(OPCode.OP_fdiv); break;
                case DOUBLE:  method.addOperation(OPCode.OP_ddiv); break;
                default: return;
            }
        }
        else if ( op.equals("%") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_irem); break;
                case LONG: method.addOperation(OPCode.OP_lrem); break;
                case FLOAT: method.addOperation(OPCode.OP_frem); break;
                case DOUBLE:  method.addOperation(OPCode.OP_drem); break;
                default: return;
            }
        }
        else if ( op.equals("~") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_ineg); break;
                case LONG: method.addOperation(OPCode.OP_lneg); break;
                default: return;
            }
        }
        else if ( op.equals("<<") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_ishl); break;
                case LONG: method.addOperation(OPCode.OP_lshl); break;
                default: return;
            }
        }
        else if ( op.equals(">>") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_ishr); break;
                case LONG: method.addOperation(OPCode.OP_lshr); break;
                default: return;
            }
        }
        else if ( op.equals(">>>") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_iushr); break;
                case LONG: method.addOperation(OPCode.OP_lushr); break;
                default: return;
            }
        }
        else if ( op.equals("&") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_iand); break;
                case LONG: method.addOperation(OPCode.OP_land); break;
                default: return;
            }
        }
        else if ( op.equals("|") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_ior); break;
                case LONG: method.addOperation(OPCode.OP_lor); break;
                default: return;
            }
        }
        else if ( op.equals("^") ) {
            switch ( type ) {
                case INT: method.addOperation(OPCode.OP_ixor); break;
                case LONG: method.addOperation(OPCode.OP_lxor); break;
                default: return;
            }
        }
    }

    public short getIP() {
        return this.method.getIP();
    }

    public void goTo(short loc) {
        System.out.println("go to " + loc);
        method.addOperation(OPCode.OP_goto, loc);
    }

    public void startCondition(String op, String _type, boolean flip) {
        Type type = typeFromString(_type);

        switch ( type ) {
            case INT:
                if ( op.equals("==") ) {
                    tmpOp.push(flip ? OPCode.OP_if_icmpne : OPCode.OP_if_icmpeq);
                }
                else if ( op.equals("!=") ) {
                    tmpOp.push(flip ? OPCode.OP_if_icmpeq : OPCode.OP_if_icmpne);
                }
                else if ( op.equals("<") ) {
                    tmpOp.push(flip ? OPCode.OP_if_icmpge : OPCode.OP_if_icmplt);
                }
                else if ( op.equals(">=") ) {
                    tmpOp.push(flip ? OPCode.OP_if_icmplt : OPCode.OP_if_icmpge);
                }
                else if ( op.equals(">") ) {
                    tmpOp.push(flip ? OPCode.OP_if_icmple : OPCode.OP_if_icmpgt);
                }
                else if ( op.equals("<=") ) {
                    tmpOp.push(flip ? OPCode.OP_if_icmpgt : OPCode.OP_if_icmple);
                }
                else {
                    tmpOp.push(flip ? OPCode.OP_if_icmpne : OPCode.OP_if_icmpeq);
                }
                break;

            case REF:
                if ( op.equals("==") ) {
                    tmpOp.push(flip ? OPCode.OP_if_acmpne : OPCode.OP_if_acmpeq);
                }
                else if ( op.equals("!=") ) {
                    tmpOp.push(flip ? OPCode.OP_if_acmpeq : OPCode.OP_if_acmpne);
                }
                else {
                    tmpOp.push(flip ? OPCode.OP_ifnonnull : OPCode.OP_ifnull);
                }
                break;
        }

        this.method.startBlock();
    }

    public void endCondition() {
        short offset = (short)(3 + this.method.getIP() - this.method.getSubIP());

        byte a = (byte) (((offset >> 8)) & 0xFF);
        byte b = (byte) (offset & 0xFF);

        this.method.addSubOperation(tmpOp.pop(), a, b);
        this.method.endBlock();
    }

    public void convert(String _from, String _to) {
        Type from = typeFromString(_from);
        Type to = typeFromString(_to);

        if ( from == to ) {
            return;
        }

        switch ( from ) {
            case INT:
                switch ( to ) {
                    case BYTE: this.method.addOperation(OPCode.OP_i2b); break;
                    case SHORT: this.method.addOperation(OPCode.OP_i2s); break;
                    case LONG: this.method.addOperation(OPCode.OP_i2l); break;
                    case FLOAT: this.method.addOperation(OPCode.OP_i2f); break;
                    case DOUBLE: this.method.addOperation(OPCode.OP_i2d); break;
                }
                break;

            case LONG:
                switch ( to ) {
                    case INT: this.method.addOperation(OPCode.OP_l2i); break;
                    case FLOAT: this.method.addOperation(OPCode.OP_l2f); break;
                    case DOUBLE: this.method.addOperation(OPCode.OP_l2d); break;
                }
                break;

            case FLOAT:
                switch ( to ) {
                    case INT: this.method.addOperation(OPCode.OP_f2i); break;
                    case LONG: this.method.addOperation(OPCode.OP_f2l); break;
                    case DOUBLE: this.method.addOperation(OPCode.OP_f2d); break;
                }
                break;

            case DOUBLE:
                switch ( to ) {
                    case INT: this.method.addOperation(OPCode.OP_d2i); break;
                    case LONG: this.method.addOperation(OPCode.OP_d2l); break;
                    case FLOAT: this.method.addOperation(OPCode.OP_d2f); break;
                }
                break;
        }
    }

    public void startFunction(String name, String type) {
        if ( method != null ) {
            System.out.println("Attempted to start a function without first completing an older one. Discarding old one.");
            method = null;
        }

        this.variables = new HashMap<String, Short>();
        this.varTypes = new HashMap<String, Type>();

        method = new VirtualMethod(name, type);
        method.setStatic(true);
    }

    public void startFunction(String name, String type, int arrayDepth) {
        if ( method != null ) {
            System.out.println("Attempted to start a function without first completing an older one. Discarding old one.");
            method = null;
        }

        this.variables = new HashMap<String, Short>();
        this.varTypes = new HashMap<String, Type>();

        method = new VirtualMethod(name, type, arrayDepth);
        method.setStatic(true);
    }

    public void addFunctionArgument(String name, String type) {
        if ( method == null ) {
            System.out.println("Attempted to add a function argument while no function was loaded.");
            return;
        }

        method.addArg(name, type);
        storeArgVariable(name, type);
    }

    public void addFunctionArgument(String name, String type, int arrayDepth) {
        if ( method == null ) {
            System.out.println("Attempted to add a function argument while no function was loaded.");
            return;
        }

        method.addArg(name, type, arrayDepth);
    }

    public void storeIntVariable(String name, int val) {
        pushInteger(val);
        storeLastVariable(name, "int");
    }

    public void storeStringVariable(String name, String val) {
        pushString(val);
        storeLastVariable(name, "String");
    }

    public void storeFloatVariable(String name, float val) {
        pushFloat(val);
        storeLastVariable(name, "float");
    }

    public void storeLastVariable(String name, String _type) {
        if ( method == null ) {
            System.out.println("Attempted to load a variable while no function was loaded.");
            return;
        }

        short local;

        Type type = typeFromString(_type);

        if ( this.variables.containsKey(name) ) {
            local = this.variables.get(name);
        }
        else {
            local = this.method.nextVariable();
            this.variables.put(name, new Short(local));
            this.varTypes.put(name, type);
        }

        switch ( type ) {
            case INT: method.addOperation(OPCode.OP_istore, (byte) local); break;
            case LONG: method.addOperation(OPCode.OP_lstore, (byte) local); break;
            case FLOAT: method.addOperation(OPCode.OP_fstore, (byte) local); break;
            case DOUBLE: method.addOperation(OPCode.OP_dstore, (byte) local); break;
            case REF: method.addOperation(OPCode.OP_astore, (byte) local); break;
            default: return;
        }
    }

    public void storeArgVariable(String name, String _type) {
        if ( method == null ) {
            System.out.println("Attempted to load a variable while no function was loaded.");
            return;
        }

        Type type = typeFromString(_type);
        short local = (short) (this.method.getVariableCount() - 1);
        this.variables.put(name, new Short(local));
        this.varTypes.put(name, type);
    }

    public void startMethodCall(String object, String field, String fieldType, String method, String methodType) {
        if ( this.currentCallMethod != -1 || this.currentCallField != -1 ) {
            return;
        }

        this.currentCallField = this.emitter.fieldReference(object, field, new VirtualField(field, fieldType).toString());
        this.currentCallMethod = this.emitter.methodReference(fieldType, method, methodType);
        this.method.addOperation(OPCode.OP_getstatic,  this.currentCallField);
    }

    public void endMethodCall() {
        if ( this.currentCallMethod == -1 || this.currentCallField == -1 ) {
            return;
        }

        this.method.addOperation(OPCode.OP_invokevirtual, this.currentCallMethod);
        this.currentCallMethod = -1;
        this.currentCallField = -1;
    }

    public String getVariableType(String variable) {
        if ( this.varTypes.containsKey(variable) ) {
            switch ( this.varTypes.get(variable) ) {
                case BYTE: return "byte";
                case SHORT: return "short";
                case INT: return "int";
                case LONG: return "long";
                case FLOAT: return "float";
                case DOUBLE: return "double";
                case CHAR: return "char";
                case REF: return "Reference";
            }
        }

        return "";
    }

    public String getFunctionType(String function) {
        if ( this.functionType.containsKey(function) ) {
            return this.functionType.get(function);
        }

        return "";
    }

    public void startFunctionCall(String method, String methodType) {
        if ( this.currentCallMethod != -1 ) {
            return;
        }

        this.currentCallMethod = this.emitter.methodReference(this.name, method, methodType);
    }

    public void endFunctionCall() {
        if ( this.currentCallMethod == -1 ) {
            return;
        }

        this.method.addOperation(OPCode.OP_invokestatic, this.currentCallMethod);
        this.currentCallMethod = -1;
    }

    public void startExternalFunctionCall(String object, String field, String fieldType, String method, String methodType) {
        if ( this.currentCallMethod != -1 ) {
            return;
        }

        this.currentCallField = this.emitter.fieldReference(object, field, new VirtualField(field, fieldType).toString());
        this.currentCallMethod = this.emitter.methodReference(fieldType, method, methodType);
        this.method.addOperation(OPCode.OP_getstatic,  this.currentCallField);
    }

    public void endExternalFunctionCall() {
        if ( this.currentCallMethod == -1 ) {
            return;
        }

        this.method.addOperation(OPCode.OP_invokestatic, this.currentCallMethod);
        this.currentCallMethod = -1;
        this.currentCallField = -1;
    }

    public void endFunction() {
        returnVoid();
        method.compileTo(this.emitter);
        this.functionType.put(method.getName(), method.getDescriptor());
        method = null;
    }

    public void returnVoid() {
        method.addOperation(OPCode.OP_return);
    }

    public void returnString(String str) {
        pushString(str);
        returnVoid();
    }

    public void returnInteger(int val) {
        pushInteger(val);
        returnVoid();
    }

    public void returnVariable(String name) {
        pushVariable(name);
        returnVoid();
    }

    public void end() {
        this.emitter.save(this.name + ".class");
    }
}
