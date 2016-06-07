package infosec.codegen;

import infosec.lexer.*;
import infosec.parser.*;
import infosec.AST.Statement.*;
import infosec.codegen.classfile.*;
import java.util.HashMap;
import java.util.Stack;
import java.lang.reflect.*;

public class CodeGen {
    private enum BasicType {
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        CHAR,
        REF;

        static BasicType fromString(String type) {
            if ( type.equals("byte") ) {
                return BasicType.BYTE;
            }
            else if ( type.equals("short") ) {
                return BasicType.SHORT;
            }
            else if ( type.equals("int") ) {
                return BasicType.INT;
            }
            else if ( type.equals("long") ) {
                return BasicType.LONG;
            }
            else if ( type.equals("float") ) {
                return BasicType.FLOAT;
            }
            else if ( type.equals("double") ) {
                return BasicType.DOUBLE;
            }
            else if ( type.equals("char") ) {
                return BasicType.CHAR;
            }

            return BasicType.REF;
        }
    };

    public enum ArrayType {
        T_BOOLEAN	((byte) 4),
        T_CHAR	    ((byte) 5),
        T_FLOAT	    ((byte) 6),
        T_DOUBLE	((byte) 7),
        T_BYTE	    ((byte) 8),
        T_SHORT	    ((byte) 9),
        T_INT	    ((byte) 10),
        T_LONG  	((byte) 11);

        private final byte code;

        ArrayType(byte code) {
            this.code = code;
        }

        public byte getCode() {
            return this.code;
        }
    };

    private class ContextState {
        private HashMap<String, Short> variables;
        private HashMap<String, BasicType> varTypes;
        private HashMap<String, String> objectClasses;
        private HashMap<String, Integer> arrayDepth;


        public ContextState() {
            this.variables = new HashMap<String, Short>();
            this.varTypes = new HashMap<String, BasicType>();
            this.objectClasses = new HashMap<String, String>();
            this.arrayDepth = new HashMap<String, Integer>();
        }

        public void setVariableIndex(String name, short val) {
            this.variables.put(name, val);
        }

        public short getVariableIndex(String name) {
            if ( this.variables.containsKey(name) ) {
                return this.variables.get(name);
            }

            return -1;
        }

        public void setVariableType(String name, BasicType type) {
            this.varTypes.put(name, type);
        }

        public BasicType getVariableType(String name) {
            if ( this.varTypes.containsKey(name) ) {
                return this.varTypes.get(name);
            }

            return null;
        }

        public void setObjectClass(String name, String val) {
            this.objectClasses.put(name, val);
        }

        public String getObjectClass(String name) {
            if ( this.objectClasses.containsKey(name) ) {
                return this.objectClasses.get(name);
            }

            return "";
        }

        public void setArrayDepth(String name, int val) {
            this.arrayDepth.put(name, val);
        }

        public int getArrayDepth(String name) {
            return this.arrayDepth.get(name).intValue();
        }

        public boolean isVariable(String name) {
            return this.variables.containsKey(name) && this.varTypes.containsKey(name);
        }
    }

    private CodeEmitter emitter;
    private VirtualMethod method;
    private String name;

    private ContextState context;
    private HashMap<String, VirtualClass> classes;
    private VirtualClass currentClass;
    private VirtualClass fileClass;

    private Stack<Short> currentCallMethod;
    private short conditionBeforeSize;
    private BasicType tmpArrayStore;
    private Stack<OPCode> tmpOp;

    public CodeGen(String name) {
        this.name = name;

        this.context = new ContextState();
        this.classes = new HashMap<String, VirtualClass>();
        this.currentClass = new VirtualClass(name);
        this.fileClass = this.currentClass;
        this.classes.put(name, this.currentClass);

        this.emitter = new CodeEmitter();
        this.emitter.thisClass(name);
        this.emitter.superClass("java/lang/Object");
        this.emitter.setSuper(true);
        this.emitter.setPublic(true);

        this.tmpOp = new Stack<OPCode>();
        this.currentCallMethod = new Stack<Short>();
        // loadJavaClass("java.lang.String");
        // loadJavaClass("java.io.PrintStream");
        // loadJavaClass("java.lang.System");
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

        if ( !this.context.isVariable(name) ) {
            return;
        }

        short local = this.context.getVariableIndex(name);

        switch ( this.context.getVariableType(name) ) {
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

    public void pushVoid() {
        this.method.addOperation(OPCode.OP_aconst_null);
    }

    public void pushArrayLength() {
        this.method.addOperation(OPCode.OP_arraylength);
    }

    public void pushNewArray(int dimmensions, String type) {
        if ( dimmensions == 1 ) {
            if ( type.equals("bool") ) {
                this.method.addOperation(OPCode.OP_newarray, ArrayType.T_BOOLEAN.getCode());
            }
            else if ( type.equals("char") ) {
                this.method.addOperation(OPCode.OP_newarray, ArrayType.T_CHAR.getCode());
            }
            else if ( type.equals("float") ) {
                this.method.addOperation(OPCode.OP_newarray, ArrayType.T_FLOAT.getCode());
            }
            else if ( type.equals("double") ) {
                this.method.addOperation(OPCode.OP_newarray, ArrayType.T_DOUBLE.getCode());
            }
            else if ( type.equals("byte") ) {
                this.method.addOperation(OPCode.OP_newarray, ArrayType.T_BYTE.getCode());
            }
            else if ( type.equals("short") ) {
                this.method.addOperation(OPCode.OP_newarray, ArrayType.T_SHORT.getCode());
            }
            else if ( type.equals("int") ) {
                this.method.addOperation(OPCode.OP_newarray, ArrayType.T_INT.getCode());
            }
            else if ( type.equals("long") ) {
                this.method.addOperation(OPCode.OP_newarray, ArrayType.T_LONG.getCode());
            }
            else {
                this.method.addOperation(OPCode.OP_anewarray, this.emitter.newClass(type));
            }
        }
        else {
            if ( BasicType.fromString(type) != BasicType.REF ) {
                type = (new VirtualField("", type, dimmensions).toString());
            }

            this.method.addOperation(OPCode.OP_multianewarray, this.emitter.newClass(type), (byte) dimmensions);
        }
    }

    public void operation(String op, String _type) {
        BasicType type = BasicType.fromString(_type);

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
        method.addOperation(OPCode.OP_goto, loc);
    }

    public void startCondition(String op, String _type, boolean flip) {
        BasicType type = BasicType.fromString(_type);

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
        BasicType from = BasicType.fromString(_from);
        BasicType to = BasicType.fromString(_to);

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

        this.context = new ContextState();

        method = new VirtualMethod(name, type);
        method.setStatic(true);
    }

    public void startFunction(String name, String type, int arrayDepth) {
        if ( method != null ) {
            System.out.println("Attempted to start a function without first completing an older one. Discarding old one.");
            method = null;
        }

        this.context = new ContextState();

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
        storeArgVariable(name, type, arrayDepth);
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

        BasicType type = BasicType.fromString(_type);

        if ( this.context.isVariable(name) ) {
            local = this.context.getVariableIndex(name);
        }
        else {
            local = this.method.nextVariable();

            this.context.setVariableIndex(name, new Short(local));
            this.context.setVariableType(name, type);
            this.context.setArrayDepth(name, new Integer(0));

            if ( type == BasicType.REF ) {
                this.context.setObjectClass(name, _type);
            }
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

    public void storeArrayVariable(String name, String _type, int arrayDepth) {
        if ( method == null ) {
            System.out.println("Attempted to load a variable while no function was loaded.");
            return;
        }

        short local;

        BasicType type = BasicType.fromString(_type);

        if ( this.context.isVariable(name) ) {
            local = this.context.getVariableIndex(name);
        }
        else {
            local = this.method.nextVariable();

            this.context.setVariableIndex(name, new Short(local));
            this.context.setVariableType(name, type);
            this.context.setArrayDepth(name, new Integer(0));

            if ( type == BasicType.REF ) {
                this.context.setObjectClass(name, _type);
            }
        }

        method.addOperation(OPCode.OP_astore, (byte) local);
    }

    public void startArrayStore(String name, int index) {
        tmpArrayStore = this.context.getVariableType(name);
        pushVariable(name);
        pushInteger(index);
    }

    public void startLastArrayStore(String type) {
        tmpArrayStore = BasicType.fromString(type);
    }

    public void endArrayStore() {
        switch ( tmpArrayStore ) {
            case INT: method.addOperation(OPCode.OP_iastore); break;
            case LONG: method.addOperation(OPCode.OP_lastore); break;
            case FLOAT: method.addOperation(OPCode.OP_fastore); break;
            case DOUBLE: method.addOperation(OPCode.OP_dastore); break;
            case REF: method.addOperation(OPCode.OP_aastore); break;
            default: return;
        }
    }

    public void loadFromArray(String _type) {
        BasicType type = BasicType.fromString(_type);

        switch ( type ) {
            case INT: method.addOperation(OPCode.OP_iaload); break;
            case LONG: method.addOperation(OPCode.OP_laload); break;
            case FLOAT: method.addOperation(OPCode.OP_faload); break;
            case DOUBLE: method.addOperation(OPCode.OP_daload); break;
            case REF: method.addOperation(OPCode.OP_aaload); break;
            default: return;
        }
    }

    public void storeDefaultVariable(String name, String _type) {
        if ( method == null ) {
            System.out.println("Attempted to load a variable while no function was loaded.");
            return;
        }

        short local;

        BasicType type = BasicType.fromString(_type);

        if ( this.context.isVariable(name) ) {
            local = this.context.getVariableIndex(name);
        }
        else {
            local = this.method.nextVariable();

            this.context.setVariableIndex(name, new Short(local));
            this.context.setVariableType(name, type);
            this.context.setArrayDepth(name, new Integer(0));

            if ( type == BasicType.REF ) {
                this.context.setObjectClass(name, _type);
            }
        }

        switch ( type ) {
            case INT: pushInteger(0); method.addOperation(OPCode.OP_istore, (byte) local); break;
            case LONG: pushInteger(0); method.addOperation(OPCode.OP_lstore, (byte) local); break;
            case FLOAT: pushFloat(0); method.addOperation(OPCode.OP_fstore, (byte) local); break;
            case DOUBLE: pushFloat(0); method.addOperation(OPCode.OP_dstore, (byte) local); break;
            case REF: pushVoid(); method.addOperation(OPCode.OP_astore, (byte) local); break;
            default: return;
        }
    }

    public void storeArgVariable(String name, String _type) {
        if ( method == null ) {
            System.out.println("Attempted to load a variable while no function was loaded.");
            return;
        }

        BasicType type = BasicType.fromString(_type);
        short local = this.method.nextVariable();

        this.context.setVariableIndex(name, new Short(local));
        this.context.setVariableType(name, type);
        this.context.setArrayDepth(name, new Integer(0));

        if ( type == BasicType.REF ) {
            this.context.setObjectClass(name, _type);
        }
    }

    public void storeArgVariable(String name, String _type, int arrayDepth) {
        if ( method == null ) {
            System.out.println("Attempted to load a variable while no function was loaded.");
            return;
        }

        BasicType type = BasicType.fromString(_type);
        short local = this.method.nextVariable();

        this.context.setVariableIndex(name, new Short(local));
        this.context.setVariableType(name, type);
        this.context.setArrayDepth(name, new Integer(arrayDepth));

        if ( type == BasicType.REF ) {
            this.context.setObjectClass(name, _type);
        }
    }

    public String getVariableType(String variable) {
        if ( this.context.isVariable(variable) ) {
            switch ( this.context.getVariableType(variable) ) {
                case BYTE:   return "byte";
                case SHORT:  return "short";
                case INT:    return "int";
                case LONG:   return "long";
                case FLOAT:  return "float";
                case DOUBLE: return "double";
                case CHAR:   return "char";
                case REF:    return this.context.getObjectClass(variable);
            }
        }

        return "";
    }

    public int getArrayDepth(String variable) {
        if ( this.context.isVariable(variable) ) {
            return this.context.getArrayDepth(variable);
        }

        return -1;
    }

    public VirtualMethod[] getFunction(String function) {
        if ( this.currentClass.getMethod(function).length > 0 ) {
            return this.currentClass.getMethod(function);
        }

        return new VirtualMethod[0];
    }

    public String[] getFunctionType(String function) {
        if ( this.currentClass.getMethodType(function).length > 0 ) {
            return this.currentClass.getMethodType(function);
        }

        return new String[0];
    }

    public void loadJavaClass(String name) {
        this.classes.put(name, VirtualClass.loadFromJava(name));
    }

    public String[] getMethodType(String object, String method) {
        if ( this.classes.containsKey(object.replace("/", ".")) ) {
            return this.classes.get(object.replace("/", ".")).getMethodType(method);
        }

        return new String[0];
    }

    public VirtualMethod[] getMethod(String object, String method) {
        if ( this.classes.containsKey(object.replace("/", ".")) ) {
            return this.classes.get(object.replace("/", ".")).getMethod(method);
        }

        return new VirtualMethod[0];
    }

    public void startFunctionCall(String method, String methodType) {
        this.currentCallMethod.push(this.emitter.methodReference(this.name, method, methodType));
    }

    public void endFunctionCall() {
        if ( this.currentCallMethod.size() == 0 ) {
            return;
        }

        this.method.addOperation(OPCode.OP_invokestatic, this.currentCallMethod.pop());
    }

    public void pushStaticField(String object, String field) {
        short id = this.emitter.fieldReference(object, field, new VirtualField(field, getField(object, field).getType()).toString());
        this.method.addOperation(OPCode.OP_getstatic, id);
    }

    public void pushField(String object, String field) {
        short id = this.emitter.fieldReference(object, field, new VirtualField(field, getField(object, field).getType()).toString());
        this.method.addOperation(OPCode.OP_getfield, id);
    }

    public String getFieldType(String object, String field) {
        object = object.replace("/", ".");

        if ( this.classes.containsKey(object) ) {
            return this.classes.get(object).getField(field).toString();
        }

        return "";
    }

    public VirtualField getField(String object, String field) {
        object = object.replace("/", ".");

        if ( this.classes.containsKey(object) ) {
            return this.classes.get(object).getField(field);
        }

        return null;
    }

    public void startMethodCall(String object, String method, String methodType) {
        this.currentCallMethod.push(this.emitter.methodReference(object, method, methodType));
    }

    public void endMethodCall() {
        this.method.addOperation(OPCode.OP_invokevirtual, this.currentCallMethod.pop());
    }

    public void endFunction() {
        if ( !this.method.hasReturn() ) {
            returnVoid();
        }

        method.compileTo(this.emitter);
        this.currentClass.addMethod(method.getName(), method);
        method = null;
    }

    public void returnVoid() {
        if ( this.method.getReturn().equals("int") ) {
            method.addOperation(OPCode.OP_ireturn);
        }
        else if ( this.method.getReturn().equals("float") ) {
            method.addOperation(OPCode.OP_freturn);
        }
        else if ( this.method.getReturn().equals("double") ) {
            method.addOperation(OPCode.OP_dreturn);
        }
        else if ( this.method.getReturn().equals("long") ) {
            method.addOperation(OPCode.OP_lreturn);
        }
        else if ( this.method.getReturn().equals("void") ){
            method.addOperation(OPCode.OP_return);
        }
        else {
            method.addOperation(OPCode.OP_areturn);
        }

        method.setHasReturn(true);
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

    public void startInitCall(String object, String type) {
        this.method.addOperation(OPCode.OP_new, this.emitter.newClass(object));
        this.dup();
        this.currentCallMethod.push(this.emitter.methodReference(object, "<init>", type));
    }

    public void endInitCall() {
        if ( this.currentCallMethod.size() == 0 ) {
            return;
        }

        this.method.addOperation(OPCode.OP_invokespecial, this.currentCallMethod.pop());
    }

    public void dup() {
        this.method.addOperation(OPCode.OP_dup);
    }

    public void end() {
        this.emitter.save(this.name + ".class");
    }
}
