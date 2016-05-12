package infosec.codegen.classfile.attributes;

import infosec.codegen.classfile.CodeException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class CodeAttribute extends Attribute {
    private short max_stack;
    private short max_locals;
    private byte[] code;
    private ArrayList<CodeException> exceptions;
    private ArrayList<Attribute> attributes;

    public CodeAttribute(short attribute_name_index, short max_stack, short max_locals) {
        super(attribute_name_index);

        this.max_stack = max_stack;
        this.max_locals = max_locals;

        this.exceptions = new ArrayList<CodeException>();
        this.attributes = new ArrayList<Attribute>();
    }

    public void setCode(byte[] code) {
        this.code = code;
    }

    public void addException(CodeException exception) {
        this.exceptions.add(exception);
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    public byte[] getAttributeBytes() {
        ArrayList<byte[]> attribute_bytes = new ArrayList<byte[]>();

        int atr_size = 0;

        for ( int i = 0; i < attributes.size(); i++ ) {
            byte[] tmp = attributes.get(i).toBytes();
            atr_size += tmp.length;
            attribute_bytes.add(tmp);
        }


        ByteBuffer tmp = ByteBuffer.allocate(2 + 2 + 4 + code.length + 2 + (exceptions.size() * 8) + 2 + atr_size)
                                   .putShort(max_stack)
                                   .putShort(max_locals)
                                   .putInt(code.length)
                                   .put(code)
                                   .putShort((short) exceptions.size());

        for ( int i = 0; i < exceptions.size(); i++ ) {
            tmp.put(exceptions.get(i).toBytes());
        }

        tmp.putShort((short) attributes.size());

        for ( int i = 0; i < attributes.size(); i++ ) {
            tmp.put(attributes.get(i).toBytes());
        }

        return tmp.array();
    }
}
