package com.maxdemarzi;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class UtilTests {

    @Test
    public void testLongToBytesAndBack() {
        byte[] bytes = Util.longToBytes(17);
        long number = Util.bytesToLong(bytes);
        assertEquals(17, number);
    }

    @Test
    public void testLongsToBytesAndBack() {
        byte[] bytes = Util.longsToBytes(17,99);
        long[] numbers = Util.bytesToLongs(bytes);
        assertEquals(17, numbers[0]);
        assertEquals(99, numbers[1]);
    }
}
