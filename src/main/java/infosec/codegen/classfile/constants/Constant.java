package infosec.codegen.classfile.constants;

public abstract class Constant {
    protected short tag_id;

    public short getTag() {
        return tag_id;
    }

    public abstract byte[] toBytes();
    public abstract String toString();
}
