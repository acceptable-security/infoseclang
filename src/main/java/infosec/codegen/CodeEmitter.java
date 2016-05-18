package infosec.codegen;

import java.io.*;
import infosec.codegen.classfile.*;
import infosec.codegen.classfile.constants.*;

public class CodeEmitter {
    private ClassEmitter classEmit;

    public CodeEmitter() {
        this.classEmit = new ClassEmitter();
    }

    public void setPublic(boolean pub) {
        if ( pub ) {
            this.classEmit.addFlag(AccessFlags.ACC_PUBLIC);
        }
        else  {
            this.classEmit.removeFlag(AccessFlags.ACC_PUBLIC);
        }
    }

    public void setSuper(boolean pub) {
        if ( pub ) {
            this.classEmit.addFlag(AccessFlags.ACC_SUPER);
        }
        else  {
            this.classEmit.removeFlag(AccessFlags.ACC_SUPER);
        }
    }

    public short utf8(String str) {
        return this.classEmit.addConstant(new Utf8Constant(str));
    }

    public short newClass(String name) {
        return this.classEmit.addConstant(new ClassInfoConstant(utf8(name)));
    }

    public short thisClass(String name) {
        return this.classEmit.addThisClass(new ClassInfoConstant(utf8(name)));
    }

    public short superClass(String name) {
        return this.classEmit.addSuperClass(new ClassInfoConstant(utf8(name)));
    }

    public short nameAndType(String name, String type) {
        return this.classEmit.addConstant(new NameAndTypeConstant(utf8(name), utf8(type)));
    }

    public short methodReference(String classN, String name, String type) {
        return this.classEmit.addConstant(new MethodRefConstant(newClass(classN), nameAndType(name, type)));
    }

    public short fieldReference(String classN, String name, String type) {
        return this.classEmit.addConstant(new FieldRefConstant(newClass(classN), nameAndType(name, type)));
    }

    public void method(MethodInfo method) {
        this.classEmit.addMethod(method);
    }

    public void field(FieldInfo field) {
        this.classEmit.addField(field);
    }

    public short string(String name) {
        return this.classEmit.addConstant(new StringConstant(utf8(name)));
    }

    public short intVal(int val) {
        return this.classEmit.addConstant(new IntegerConstant(val));
    }

    public short floatVal(float val) {
        return this.classEmit.addConstant(new FloatConstant(val));
    }

    public void save(String filename) {
        try {
            this.classEmit.flush();
            this.classEmit.save(filename);
        }
        catch ( IOException e ) {
            System.out.println("Failed.");
        }
    }
}
