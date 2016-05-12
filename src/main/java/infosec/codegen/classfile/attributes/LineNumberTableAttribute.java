package infosec.codegen.classfile.attributes;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class LineNumberTableAttribute extends Attribute {
    private ArrayList<Short> pcs;
    private ArrayList<Short> line_numbers;

    public LineNumberTableAttribute(short attribute_name_index) {
        super(attribute_name_index);

        this.pcs = new ArrayList<Short>();
        this.line_numbers = new ArrayList<Short>();
    }

    public void addLine(short pc, short line_number) {
        this.pcs.add(new Short(pc));
        this.line_numbers.add(new Short(line_number));
    }

    public byte[] getAttributeBytes() {
        ByteBuffer out = ByteBuffer.allocate(2 + (4 * this.pcs.size()));

        out.putShort((short) pcs.size());

        for ( int i = 0; i < this.pcs.size(); i++ ) {
            out.putShort(this.pcs.get(i).shortValue());
            out.putShort(this.line_numbers.get(i).shortValue());
        }

        return out.array();
    }
}
