package infosec.codegen.classfile.attributes;

import java.nio.ByteBuffer;

public class SourceFileAttribute extends Attribute {
    private short sourcefile_index;

    public SourceFileAttribute(short attribute_name_index, short sourcefile_index) {
        super(attribute_name_index);
        this.sourcefile_index = sourcefile_index;
    }

    public byte[] getAttributeBytes() {
        return ByteBuffer.allocate(2).putShort(sourcefile_index).array();
    }
}
