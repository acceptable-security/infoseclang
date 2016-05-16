package infosec.codegen;

import infosec.codegen.classfile.*;
import infosec.codegen.classfile.attributes.*;
import infosec.codegen.classfile.constants.*;

public class VirtualField {
    private String name;
    private String type;
    private int arrayDepth;

    public VirtualField(String name, String type) {
        this.name = name;
        this.type = type;
        this.arrayDepth = 0;
    }

    public VirtualField(String name, String type, int arrayDepth) {
        this.name = name;
        this.type = type;
        this.arrayDepth = arrayDepth;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String toString() {
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

        for ( int i = 0; i < arrayDepth; i++ ) {
            out += "[";
        }

        for ( int i = 0; i < names.length; i++ ) {
            if ( this.type.equals(names[i]) ) {
                out += types[i];
                return out;
            }
        }

        out += "L" + type + ";";

        return out;
    }

    public void compileTo(CodeGen codegen) {
        FieldInfo field = new FieldInfo(codegen.storeString(name), codegen.storeString(toString()));
        codegen.storeField(field);
    }
}
