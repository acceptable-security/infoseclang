package infosec.codegen;

import java.util.ArrayList;
import java.nio.file.*;
import java.nio.*;
import java.io.*;

public class Emitter {
    private ByteArrayOutputStream baos;
    private DataOutputStream dos;

    public Emitter() {
        this.baos = new ByteArrayOutputStream();
        this.dos = new DataOutputStream(this.baos);
    }

    public void emit(byte[] bytes) throws IOException {
        this.dos.write(bytes);
    }

    public void emit(double value) throws IOException {
        this.dos.writeDouble(value);
    }

    public void emit(long value) throws IOException {
        this.dos.writeLong(value);
    }

    public void emit(float value) throws IOException {
        this.dos.writeFloat(value);
    }

    public void emit(int value) throws IOException {
        this.dos.writeInt(value);
    }

    public void emit(short value) throws IOException {
        this.dos.writeShort(value);
    }

    public void emit(byte value) throws IOException {
        this.dos.writeByte(value);
    }

    public void emit(String code) throws IOException {
        byte[] bytes = code.getBytes("UTF-8");
        emit((short) bytes.length);
        emit(bytes);
    }

    public void emit(OPCode op, byte[] args) throws IOException {
        emit((byte) op.getOP());
        emit(args);
    }

    public void save(String path) throws IOException {
        this.dos.flush();
        Files.write(Paths.get(path), this.baos.toByteArray());
    }
}
