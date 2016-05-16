package infosec.codegen.classfile.constants;

import java.nio.ByteBuffer;

public class InterfaceMethodRefConstant extends Constant {
    private short class_index;
    private short name_and_type_index;

    public InterfaceMethodRefConstant(short class_index, short name_and_type_index) {
        this.class_index = class_index;
        this.name_and_type_index = name_and_type_index;
        this.tag_id = 11;
    }

    public byte[] toBytes() {
        byte[] tmp = ByteBuffer.allocate(4).putShort(class_index).putShort(name_and_type_index).array();
        byte[] out = new byte[5];

        out[0] = (byte) this.tag_id;
        out[1] = tmp[0];
        out[2] = tmp[1];
        out[3] = tmp[2];
        out[4] = tmp[3];

        return out;
    }

    public String toString() {
        return "InterfaceMethodRefConstant<" + class_index + ", " + name_and_type_index + ">";
    }
}
