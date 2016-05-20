package infosec.codegen;

import infosec.codegen.classfile.*;
import infosec.codegen.classfile.attributes.*;
import infosec.codegen.classfile.constants.*;
import java.util.ArrayList;

public class VirtualBlock {
    private ArrayList<Byte> byteCode;
    private VirtualBlock internalBlock = null;
    private short variableCount = 0;

    public VirtualBlock(short variableCount) {
        this.byteCode = new ArrayList<Byte>();
        this.variableCount = 0;
    }

    public void startBlock() {
        if ( internalBlock != null ) {
            internalBlock.startBlock();
        }
        else {
            internalBlock = new VirtualBlock(this.variableCount);
        }
    }

    public int getInternalBlockSize() {
        int cnt = this.byteCode.size();

        if ( internalBlock != null ) {
            cnt += internalBlock.getInternalBlockSize();
        }

        return cnt;
    }

    public byte[] getInternalByteCode() {
        byte[] my = byteCode();

        if ( internalBlock != null ) {
            byte[] their = internalBlock.getInternalByteCode();
            byte[] tmp = new byte[my.length + their.length];

            for ( int i = 0; i < my.length + their.length; i++ ) {
                if ( i < my.length ) {
                    tmp[i] = my[i];
                }
                else {
                    tmp[i] = their[i - my.length];
                }
            }

            my = their;
        }

        return my;
    }

    public void endBlock() {
        if ( internalBlock == null ) {
            return;
        }

        byte[] up = getInternalByteCode();

        for ( int i = 0; i < up.length; i++ ) {
            this.byteCode.add(up[i]);
        }

        internalBlock = null;
    }

    public void addRealOperation(OPCode op) {
        this.byteCode.add((byte) op.getOP());
    }

    public void addRealOperation(OPCode op, byte arg) {
        this.byteCode.add((byte) op.getOP());
        this.byteCode.add(arg);
    }

    public void addRealOperation(OPCode op, short arg) {
        this.byteCode.add((byte) op.getOP());
        this.byteCode.add((byte) ((arg >> 8) & 0xFF));
        this.byteCode.add((byte) (arg & 0xFF));
    }

    public void addRealOperation(OPCode op, byte arg, byte arg2) {
        this.byteCode.add((byte) op.getOP());
        this.byteCode.add(arg);
        this.byteCode.add(arg2);
    }

    public void addOperation(OPCode op) {
        if ( this.internalBlock != null ) {
            this.internalBlock.addOperation(op);
            return;
        }
        this.byteCode.add((byte) op.getOP());
    }

    public void addOperation(OPCode op, byte arg) {
        if ( this.internalBlock != null ) {
            this.internalBlock.addOperation(op, arg);
            return;
        }
        this.byteCode.add((byte) op.getOP());
        this.byteCode.add(arg);
    }

    public void addOperation(OPCode op, short arg) {
        if ( this.internalBlock != null ) {
            this.internalBlock.addOperation(op, arg);
            return;
        }
        this.byteCode.add((byte) op.getOP());
        this.byteCode.add((byte) ((arg >> 8) & 0xFF));
        this.byteCode.add((byte) (arg & 0xFF));
    }

    public void addOperation(OPCode op, byte arg, byte arg2) {
        if ( this.internalBlock != null ) {
            this.internalBlock.addOperation(op, arg, arg2);
            return;
        }
        this.byteCode.add((byte) op.getOP());
        this.byteCode.add(arg);
        this.byteCode.add(arg2);
    }

    public short getIP() {
        return (short) this.byteCode.size();
    }

    public byte[] byteCode() {
        byte[] bytes = new byte[byteCode.size()];

        for ( int i = 0; i < byteCode.size(); i++ ) {
            bytes[i] = byteCode.get(i).byteValue();
        }

        return bytes;
    }

    public short nextVariable() {
        if ( this.internalBlock != null ) {
            return this.internalBlock.nextVariable();
        }

        return variableCount++;
    }

    public short getVariableCount() {
        return variableCount;
    }
}
