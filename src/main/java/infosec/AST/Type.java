package infosec.AST;

public class Type {
    private String type;
    private int arrayDepth = 0;

    public Type(String type) {
        if ( type.equals("string") ) {
            type = "java/lang/String";
        }

        this.type = type;
    }

    public Type(String type, int arrayDepth) {
        if ( type.equals("string") ) {
            type = "java/lang/String";
        }
        
        this.type = type;
        this.arrayDepth = arrayDepth;
    }

    public String getBasicType() {
        return type;
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public String toString() {
        String out = type;

        for ( int i = 0; i < arrayDepth; i++ ) {
            out += "[]";
        }

        return out;
    }
}
