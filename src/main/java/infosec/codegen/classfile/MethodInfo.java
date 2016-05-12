package infosec.codegen.classfile;

import infosec.codegen.classfile.attributes.*;
import java.util.ArrayList;
import java.nio.ByteBuffer;

public class MethodInfo {
    private short access_flags;
    private short name_index;
    private short descriptor_index;
    private ArrayList<Attribute> attributes;

    public MethodInfo(short name_index, short descriptor_index) {
        this.name_index = name_index;
        this.descriptor_index = descriptor_index;
        this.attributes = new ArrayList<Attribute>();
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    public void addFlag(AccessFlags flag) {
        access_flags |= flag.getValue();
    }

    public void removeFlag(AccessFlags flag) {
        access_flags &= ~flag.getValue();
    }

    public byte[] toBytes() {
        ArrayList<byte[]> attribute_bytes = new ArrayList<byte[]>();

        int atr_size = 0;

        for ( int i = 0; i < attributes.size(); i++ ) {
            byte[] tmp = attributes.get(i).toBytes();
            atr_size += tmp.length;
            attribute_bytes.add(tmp);
        }

        ByteBuffer tmp = ByteBuffer.allocate(2 + 2 + 2 + 2 + atr_size)
                                   .putShort(access_flags)
                                   .putShort(name_index)
                                   .putShort(descriptor_index)
                                   .putShort((short) attributes.size());

        for ( int i = 0; i < attribute_bytes.size(); i++ ) {
            tmp.put(attribute_bytes.get(i));
        }

        return tmp.array();
    }
}
