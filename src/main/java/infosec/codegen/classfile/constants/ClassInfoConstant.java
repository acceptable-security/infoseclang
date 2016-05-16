package infosec.codegen.classfile.constants;

import java.nio.ByteBuffer;

public class ClassInfoConstant extends Constant {
    private short name_index;

    public ClassInfoConstant(short name_index) {
        this.name_index = name_index;
        this.tag_id = 7;
    }

    public byte[] toBytes() {
        byte[] tmp = ByteBuffer.allocate(2).putShort(name_index).array();
        byte[] out = new byte[3];

        out[0] = (byte) this.tag_id;
        out[1] = tmp[0];
        out[2] = tmp[1];

        return out;
    }

    public String toString() {
        return "ClassInfo<" + name_index + ">";
    }
}
