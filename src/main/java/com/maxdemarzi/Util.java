package com.maxdemarzi;

public class Util {


    public static byte[] longToBytes(long l) {
        byte[] result = new byte[Long.BYTES];
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= Long.BYTES;
        }
        return result;
    }

    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    public static byte[] longsToBytes(long l1, long l2) {
        byte[] result = new byte[Long.BYTES * 2];
        for (int i = (Long.BYTES) - 1; i >= 0; i--) {
            result[i] = (byte)(l1 & 0xFF);
            l1 >>= Long.BYTES;
        }

        for (int i = (2 * Long.BYTES) - 1; i >= Long.BYTES; i--) {
            result[i] = (byte)(l2 & 0xFF);
            l2 >>= Long.BYTES;
        }

        return result;
    }

    public static long[] bytesToLongs(byte[] b) {
        long result1 = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            result1 <<= Byte.SIZE;
            result1 |= (b[i] & 0xFF);
        }
        long result2 = 0;
        for (int i = Long.BYTES; i < (2 * Long.BYTES); i++) {
            result2 <<= Byte.SIZE;
            result2 |= (b[i] & 0xFF);
        }

        return new long[]{result1, result2};
    }
}
