package infosec.codegen;

import java.util.*;
import java.lang.reflect.*;

class VirtualClass {
    private HashMap<String, ArrayList<VirtualMethod>> methods;
    private HashMap<String, VirtualField> fields;

    private String name;

    public VirtualClass(String name) {
        this.name = name.replace("/", ".");
        this.methods = new HashMap<String, ArrayList<VirtualMethod>>();
        this.fields = new HashMap<String, VirtualField>();
    }

    public void addMethod(String name, VirtualMethod meth) {
        if ( methods.containsKey(name) ) {
            methods.get(name).add(meth);
        }
        else {
            ArrayList<VirtualMethod> tmp = new ArrayList<VirtualMethod>();
            tmp.add(meth);
            methods.put(name, tmp);
        }
    }

    public void addField(String name, VirtualField field) {
        this.fields.put(name, field);
    }

    public VirtualField getField(String name) {
        return this.fields.get(name);
    }

    public static VirtualClass loadFromJava(String name) {
        Method[] loads;
        Field[] fields;

        try {
            loads = Class.forName(name).getDeclaredMethods();
            fields = Class.forName(name).getFields();
        }
        catch ( ClassNotFoundException e ) {
            return null;
        }

        VirtualClass cls = new VirtualClass(name);

        for ( int i = 0; i < loads.length; i++ ) {
            VirtualMethod vm = new VirtualMethod(loads[i].getName(), loads[i].getReturnType().getCanonicalName());
            Class<?>[] pType  = loads[i].getParameterTypes();

            for ( int j = 0; j < pType.length; j++ ) {
                vm.addArg("", pType[j].getCanonicalName());
            }

            cls.addMethod(loads[i].getName(), vm);
        }

        for ( int i = 0; i < fields.length; i++ ) {
            cls.addField(fields[i].getName(), new VirtualField(fields[i].getName(), fields[i].getType().getCanonicalName(), Modifier.isStatic(fields[i].getModifiers())));
        }

        return cls;
    }

    public String[] getMethodType(String name) {
        ArrayList<String> tmp = new ArrayList<String>();

        if ( this.methods.containsKey(name) ) {
            for ( int i = 0; i < this.methods.get(name).size(); i++ ) {
                tmp.add(this.methods.get(name).get(i).getDescriptor());
            }
        }

        return tmp.toArray(new String[tmp.size()]);
    }

    public VirtualMethod[] getMethod(String name) {
        ArrayList<VirtualMethod> tmp = new ArrayList<VirtualMethod>();

        if ( this.methods.containsKey(name) ) {
            for ( int i = 0; i < this.methods.get(name).size(); i++ ) {
                tmp.add(this.methods.get(name).get(i));
            }
        }

        return tmp.toArray(new VirtualMethod[tmp.size()]);
    }
}
