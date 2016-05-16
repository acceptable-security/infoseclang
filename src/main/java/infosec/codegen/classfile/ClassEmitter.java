package infosec.codegen.classfile;

import infosec.codegen.Emitter;
import infosec.codegen.classfile.constants.*;
import infosec.codegen.classfile.attributes.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;

public class ClassEmitter extends Emitter {
    private final short minor_version = 3;
    private final short major_version = 45;

    private short access_flags;
    private short this_class = 0;
    private short super_class = 0;

    private ArrayList<Constant> constants;
    private ArrayList<Short> interfaces;
    private ArrayList<Attribute> attributes;
    private ArrayList<FieldInfo> fields;
    private ArrayList<MethodInfo> methods;

    private int debugLevel = 0;

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

    public short addConstant(Constant constant) {
        for ( int i = 0; i < this.constants.size(); i++ ) {
            if ( this.constants.get(i).toBytes().equals(constant.toBytes()) ) {
                return (short) (i + 1);
            }
        }

        this.constants.add(constant);
        return (short) (this.constants.size());
    }

    public short addThisClass(Constant constant) {
        this_class = addConstant(constant);
        return this_class;
    }

    public short addSuperClass(Constant constant) {
        super_class = addConstant(constant);
        return super_class;
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

    public void debug(int level, String msg) {
        if ( level >= debugLevel ) {
            System.out.println("[DEBUG] " + msg);
        }
    }

    public void flush() throws IOException {
        emit((byte) 0xCA);
        emit((byte) 0xFE);
        emit((byte) 0xBA);
        emit((byte) 0xBE);

        debug(1, "Version " + major_version + "." + minor_version);
        emit(minor_version);
        emit(major_version);

        debug(1, "Emitting " + constants.size() + " constants");
        emit((short) (constants.size() + 1));

        for ( int i = 0; i < constants.size(); i++ ) {
            debug(1, "Constant #" + (i + 1) + ": " + constants.get(i));
            debug(2, "Bytes: " + Arrays.toString(constants.get(i).toBytes()));
            emit(constants.get(i).toBytes());
        }

        debug(1, "Class Flags: " + access_flags);
        emit(access_flags);
        debug(1, "This Class: " + this_class);
        emit(this_class);
        debug(1, "Super Class: " + super_class);
        emit(super_class);

        debug(1, "Emitting " + interfaces.size() + " interfaces");
        emit((short) interfaces.size());

        for ( int i = 0; i < interfaces.size(); i++ ) {
            debug(2, "Interface value " + this.interfaces.get(i).shortValue());
            emit(this.interfaces.get(i).shortValue());
        }

        debug(1, "Emitting " + fields.size() + " fields");
        emit((short) fields.size());

        for ( int i = 0; i < fields.size(); i++ ) {
            emit(fields.get(i).toBytes());
        }

        debug(1, "Emitting " + methods.size() + " methods");
        emit((short) methods.size());

        for ( int i = 0; i < methods.size(); i++ ) {
            emit(methods.get(i).toBytes());
        }

        debug(1, "Emitting " + attributes.size() + " attributes");
        emit((short) attributes.size());

        for ( int i = 0; i < attributes.size(); i++ ) {
            emit(attributes.get(i).toBytes());
        }
    }
}
