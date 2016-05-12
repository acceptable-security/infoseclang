/**
 * Note: this is going to be completely implicit, because I don't want to waste time on this yet.
 */

package infosec.codegen.classfile.attributes;

import java.nio.ByteBuffer;

public class StackMapAttribute extends Attribute {
    public StackMapAttribute(short attribute_name_index) {
        super(attribute_name_index);
    }

    public byte[] getAttributeBytes() {
        return ByteBuffer.allocate(2).putShort((short) 0).array();
    }
}
