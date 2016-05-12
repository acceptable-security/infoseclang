package infosec.codegen.classfile;

import java.nio.ByteBuffer;

public class CodeException {
    private short start_pc;
    private short end_pc;
    private short handler_pc;
    private short catch_type;

    public CodeException(short start_pc, short end_pc, short handler_pc, short catch_type) {
        this.start_pc = start_pc;
        this.end_pc = end_pc;
        this.handler_pc = handler_pc;
        this.catch_type = catch_type;
    }

    public byte[] toBytes() {
        return ByteBuffer.allocate(8).putShort(start_pc)
                                     .putShort(end_pc)
                                     .putShort(handler_pc)
                                     .putShort(catch_type).array();
    }
}
