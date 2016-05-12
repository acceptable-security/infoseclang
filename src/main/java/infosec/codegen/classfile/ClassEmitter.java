package infosec.codegen.classfile;

import infosec.codegen.Emitter;
import infosec.codegen.classfile.constants.*;
import infosec.codegen.classfile.attributes.*;
import java.util.ArrayList;
import java.io.IOException;

public class ClassEmitter extends Emitter {
    private final short minor_version = 45;
    private final short major_version = 0;

    private short access_flags;
    private short this_class = 0;
    private short super_class = 0;

    private ArrayList<Constant> constants;
    private ArrayList<Short> interfaces;
    private ArrayList<Attribute> attributes;
    private ArrayList<FieldInfo> fields;
    private ArrayList<MethodInfo> methods;

    public ClassEmitter() {
        super();
        constants = new ArrayList<Constant>();
        interfaces = new ArrayList<Short>();
        attributes = new ArrayList<Attribute>();
        fields = new ArrayList<FieldInfo>();
        methods = new ArrayList<MethodInfo>();
    }

    public void addFlag(AccessFlags flag) {
        access_flags |= flag.getValue();
    }

    public void removeFlag(AccessFlags flag) {
        access_flags &= ~flag.getValue();
    }

    public void addConstant(Constant constant) {
        this.constants.add(constant);
    }

    public void addThisClass(Constant constant) {
        this.constants.add(constant);
        this_class = (short) (this.constants.size() - 1);
    }

    public void addSuperClass(Constant constant) {
        this.constants.add(constant);
        super_class = (short) (this.constants.size() - 1);
    }

    public void addInterface(Constant constant) {
        this.constants.add(constant);
        this.interfaces.add(new Short((short) (this.constants.size() - 1)));
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    public void addField(FieldInfo field) {
        this.fields.add(field);
    }

    public void addMethod(MethodInfo method) {
        this.methods.add(method);
    }

    public void flush() throws IOException {
        emit((byte) 0xCA);
        emit((byte) 0xFE);
        emit((byte) 0xBA);
        emit((byte) 0xBE);

        emit(minor_version);
        emit(major_version);

        emit((short) constants.size());

        for ( int i = 0; i < constants.size(); i++ ) {
            emit(constants.get(i).toBytes());
        }

        emit(access_flags);
        emit(this_class);
        emit(super_class);

        emit((short) interfaces.size());

        for ( int i = 0; i < interfaces.size(); i++ ) {
            emit(this.interfaces.get(i).shortValue());
        }

        emit((short) fields.size());

        for ( int i = 0; i < fields.size(); i++ ) {
            emit(fields.get(i).toBytes());
        }

        emit((short) methods.size());

        for ( int i = 0; i < methods.size(); i++ ) {
            emit(methods.get(i).toBytes());
        }

        emit((short) attributes.size());

        for ( int i = 0; i < attributes.size(); i++ ) {
            emit(attributes.get(i).toBytes());
        }
    }
}
