package infosec.codegen.classfile.constants;

import java.nio.ByteBuffer;

public class NameAndTypeConstant extends Constant {
    private short name_index;
    private short type_index;

    public NameAndTypeConstant(short name_index, short type_index) {
        this.name_index = name_index;
        this.type_index = type_index;
        this.tag_id = 12;
    }

    public byte[] toBytes() {
        byte[] tmp = ByteBuffer.allocate(4).putShort(name_index).putShort(type_index).array();
        byte[] out = new byte[5];

        out[0] = (byte) this.tag_id;
        out[1] = tmp[0];
        out[2] = tmp[1];
        out[3] = tmp[2];
        out[4] = tmp[3];

        return out;
    }

    public String toString() {
        return "NameAndTypeConstant<" + name_index + ", " + type_index + ">";
    }
}
