package infosec.codegen.classfile.constants;

import java.nio.ByteBuffer;

public class IntegerConstant extends Constant {
    private int value;

    public IntegerConstant(int value) {
        this.value = value;
        this.tag_id = 3;
    }

    public byte[] toBytes() {
        byte[] tmp = ByteBuffer.allocate(4).putInt(value).array();
        byte[] out = new byte[5];

        out[0] = (byte) this.tag_id;
        out[1] = tmp[0];
        out[2] = tmp[1];
        out[3] = tmp[2];
        out[4] = tmp[3];

        return out;
    }
}
