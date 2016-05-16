package infosec.codegen;

import java.io.*;
import infosec.codegen.classfile.*;
import infosec.codegen.classfile.constants.*;

public class CodeGen {
    private ClassEmitter classEmit;

    public CodeGen() {
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

    public short storeString(String str) {
        return this.classEmit.addConstant(new Utf8Constant(str));
    }

    public short newClass(String name) {
        return this.classEmit.addConstant(new ClassInfoConstant(storeString(name)));
    }

    public short newClass(String name, boolean isSuper) {
        if ( isSuper ) {
            return this.classEmit.addSuperClass(new ClassInfoConstant(storeString(name)));
        }

        return this.classEmit.addThisClass(new ClassInfoConstant(storeString(name)));
    }

    public short newNameAndType(String name, String type) {
        return this.classEmit.addConstant(new NameAndTypeConstant(storeString(name), storeString(type)));
    }

    public short newMethodReference(short classN, String name, String type) {
        return this.classEmit.addConstant(new MethodRefConstant(classN, newNameAndType(name, type)));
    }

    public short newFieldReference(short classN, String name, String type) {
        return this.classEmit.addConstant(new FieldRefConstant(classN, newNameAndType(name, type)));
    }

    public void storeMethod(MethodInfo method) {
        this.classEmit.addMethod(method);
    }

    public void storeField(FieldInfo field) {
        this.classEmit.addField(field);
    }

    public short newString(String name) {
        return this.classEmit.addConstant(new StringConstant(storeString(name)));
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
