package infosec.codegen.classfile;

import java.nio.ByteBuffer;

public class InnerClass {
    private short inner_class_info_index;
    private short outer_class_info_index;
    private short inner_name_index;
    private short inner_class_access_flags;

    public InnerClass(short inner_class_info_index, short outer_class_info_index, short inner_name_index) {
        this.inner_class_info_index = inner_class_info_index;
        this.outer_class_info_index = outer_class_info_index;
        this.inner_name_index = inner_name_index;
    }

    public void addFlag(AccessFlags flag) {
        inner_class_access_flags |= flag.getValue();
    }

    public void removeFlag(AccessFlags flag) {
        inner_class_access_flags &= ~flag.getValue();
    }

    public byte[] toBytes() {
        return ByteBuffer.allocate(8).putShort(inner_class_info_index)
                                     .putShort(outer_class_info_index)
                                     .putShort(inner_name_index)
                                     .putShort(inner_class_access_flags).array();
    }
}
