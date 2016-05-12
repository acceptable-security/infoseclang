package infosec.codegen.classfile.constants;

import java.nio.ByteBuffer;

public class FloatConstant extends Constant {
    private float value;

    public FloatConstant(float value) {
        this.value = value;
        this.tag_id = 4;
    }

    public byte[] toBytes() {
        byte[] tmp = ByteBuffer.allocate(4).putFloat(value).array();
        byte[] out = new byte[5];

        out[0] = (byte) this.tag_id;
        out[1] = tmp[0];
        out[2] = tmp[1];
        out[3] = tmp[2];
        out[4] = tmp[3];

        return out;
    }
}
