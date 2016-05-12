package infosec.codegen.classfile.attributes;

import java.nio.ByteBuffer;

public class ConstantValueAttribute extends Attribute {
    private short constantvalue_index;

    public ConstantValueAttribute(short attribute_name_index, short constantvalue_index) {
        super(attribute_name_index);
        this.constantvalue_index = constantvalue_index;
    }

    public byte[] getAttributeBytes() {
        return ByteBuffer.allocate(2).putShort(constantvalue_index).array();
    }
}
