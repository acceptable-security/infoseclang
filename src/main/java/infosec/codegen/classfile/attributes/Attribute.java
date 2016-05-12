package infosec.codegen.classfile.attributes;

import java.nio.ByteBuffer;

public abstract class Attribute {
    private short attribute_name_index;

    public Attribute(short attribute_name_index) {
        this.attribute_name_index = attribute_name_index;
    }

    public abstract byte[] getAttributeBytes();

    public byte[] toBytes() {
        byte[] bytes = getAttributeBytes();

        return ByteBuffer.allocate(6 + bytes.length).putShort(attribute_name_index)
                                                    .putInt(bytes.length)
                                                    .put(bytes).array();
    }
}
