package infosec.codegen.classfile.constants;

import java.io.UnsupportedEncodingException;

public class Utf8Constant extends Constant {
    private String str;

    public Utf8Constant(String str) {
        this.tag_id = 1;
        this.str = str;
    }

    public byte[] toBytes() {
        byte[] tmp = null;

        try {
            tmp = this.str.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }

        byte[] out = new byte[tmp.length + 3];

        out[0] = (byte) this.tag_id;
        out[1] = (byte) ((tmp.length >> 8) & 0xFF);
        out[2] = (byte) (tmp.length & 0xFF);

        for ( int i = 0; i < tmp.length; i++ ) {
            out[i + 3] = tmp[i];
        }

        return out;
    }
}
