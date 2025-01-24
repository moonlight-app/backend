package ru.moonlightapp.backend.core.util;

import com.google.protobuf.ByteString;

import java.io.*;

public final class ProtoBytesConverter {

    public static <T> T fromByteString(ByteString byteString, Class<T> clazz) throws IOException, ClassNotFoundException {
        if (byteString == null)
            return null;

        return fromBytes(byteString.toByteArray(), clazz);
    }

    public static Object fromByteString(ByteString byteString) throws IOException, ClassNotFoundException {
        if (byteString == null)
            return null;

        return fromBytes(byteString.toByteArray());
    }

    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException {
        return clazz.cast(fromBytes(bytes));
    }

    public static Object fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }

    public static ByteString toByteString(Object object) throws IOException {
        if (object == null)
            return null;

        return ByteString.copyFrom(toBytes(object));
    }

    public static byte[] toBytes(Object object) throws IOException {
        if (object == null)
            return null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.flush();

        return baos.toByteArray();
    }

}
