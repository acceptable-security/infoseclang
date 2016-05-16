package infosec.codegen.classfile.constants;

import java.nio.ByteBuffer;

public class InvokeDynamicConstant extends Constant {
    private short bootstrap_method_attr_index;
    private short name_and_type_index;

    public InvokeDynamicConstant(short bootstrap_method_attr_index, short name_and_type_index) {
        this.bootstrap_method_attr_index = bootstrap_method_attr_index;
        this.name_and_type_index = name_and_type_index;
        this.tag_id = 18;
    }

    public byte[] toBytes() {
        byte[] tmp = ByteBuffer.allocate(4).putShort(bootstrap_method_attr_index).putShort(name_and_type_index).array();
        byte[] out = new byte[4];

        out[0] = (byte) this.tag_id;
        out[1] = tmp[0];
        out[2] = tmp[1];
        out[3] = tmp[2];

        return out;
    }

    public String toString() {
        return "InvokeDynamicConstant<" + bootstrap_method_attr_index + ", " + name_and_type_index;
    }
}
