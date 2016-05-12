package infosec.codegen.classfile.constants;

import java.nio.ByteBuffer;

public class StringConstant extends Constant {
    private short string_index;

    public StringConstant(short string_index) {
        this.string_index = string_index;
        this.tag_id = 8;
    }

    public byte[] toBytes() {
        byte[] tmp = ByteBuffer.allocate(2).putShort(string_index).array();
        byte[] out = new byte[3];

        out[0] = (byte) this.tag_id;
        out[1] = tmp[0];
        out[2] = tmp[1];

        return out;
    }
}
