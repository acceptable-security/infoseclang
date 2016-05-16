package infosec.codegen.classfile.constants;

import java.nio.ByteBuffer;

public class MethodHandleConstant extends Constant {
    private byte reference_kind;
    private short reference_index;

    public MethodHandleConstant(byte reference_kind, short reference_index) {
        this.reference_kind = reference_kind;
        this.reference_index = reference_index;
        this.tag_id = 15;
    }

    public byte[] toBytes() {
        byte[] tmp = ByteBuffer.allocate(3).put(reference_kind).putShort(reference_index).array();
        byte[] out = new byte[4];

        out[0] = (byte) this.tag_id;
        out[1] = tmp[0];
        out[2] = tmp[1];
        out[3] = tmp[2];

        return out;
    }

    public String toString() {
        return "MethodHandleConstant<" + reference_kind + ", " + reference_index + ">";
    }
}
