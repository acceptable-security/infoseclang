package infosec.codegen.classfile.attributes;

import infosec.codegen.classfile.AccessFlags;
import infosec.codegen.classfile.InnerClass;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class InnerClassAttribute extends Attribute {
    private ArrayList<InnerClass> innerClasses;

    public InnerClassAttribute(short attribute_name_index) {
        super(attribute_name_index);
        this.innerClasses = new ArrayList<InnerClass>();
    }

    public void addInnerClass(InnerClass inner) {
        this.innerClasses.add(inner);
    }

    public byte[] getAttributeBytes() {
        ByteBuffer out = ByteBuffer.allocate(2 + (this.innerClasses.size() * 4));
        out.putShort((short) this.innerClasses.size());

        for ( int i = 0; i < this.innerClasses.size(); i++ ) {
            out.put(this.innerClasses.get(i).toBytes());
        }

        return out.array();
    }
}
