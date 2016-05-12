package infosec.codegen.classfile.attributes;

import java.nio.ByteBuffer;

public class EnclosingMethodAttribute extends Attribute {
    private short class_index;
    private short method_index;

    public EnclosingMethodAttribute(short attribute_name_index, short class_index, short method_index) {
        super(attribute_name_index);

        this.class_index = class_index;
        this.method_index = method_index;
    }

    public byte[] getAttributeBytes() {
        return ByteBuffer.allocate(4).putShort(class_index)
                                     .putShort(method_index).array();
    }
}
