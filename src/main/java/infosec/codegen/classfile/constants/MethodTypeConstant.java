package infosec.codegen.classfile.constants;

import java.nio.ByteBuffer;

public class MethodTypeConstant extends Constant {
    private short descriptor_index;

    public MethodTypeConstant(short descriptor_index) {
        this.descriptor_index = descriptor_index;
        this.tag_id = 16;
    }

    public byte[] toBytes() {
        byte[] tmp = ByteBuffer.allocate(2).putShort(descriptor_index).array();
        byte[] out = new byte[3];

        out[0] = (byte) this.tag_id;
        out[1] = tmp[0];
        out[2] = tmp[1];

        return out;
    }
}
