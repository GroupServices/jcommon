package org.group.jcommon.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EnumUtilTest {
    enum Code {
        OK,
        BAD_REQUEST,
        BAD_VALUE
    }

    @Test
    public void testFromName() {
        Code c = EnumUtil.fromNameOr("BAD_REQUEST", Code.class, Code.OK);
        assertEquals(Code.BAD_REQUEST, c);

        c = EnumUtil.fromNameOr("BAD_VALUE", Code.class, Code.OK);
        assertEquals(Code.BAD_VALUE, c);

        c = EnumUtil.fromNameOr("OK", Code.class, Code.OK);
        assertEquals(Code.OK, c);
    }

    @Test
    public void testFromBadName() {
        Code c = EnumUtil.fromNameOr("GOOD_REQUEST", Code.class, Code.OK);
        assertEquals(Code.OK, c);

        c = EnumUtil.fromNameOr("crap", Code.class, Code.OK);
        assertEquals(Code.OK, c);

        c = EnumUtil.fromNameOr("", Code.class, Code.OK);
        assertEquals(Code.OK, c);

        c = EnumUtil.fromNameOr(null, Code.class, Code.OK);
        assertEquals(Code.OK, c);
    }

    @Test
    public void testFromOrdinal() {
        assertEquals(Code.BAD_REQUEST, EnumUtil.fromOrdinalOrNull(0, Code.class));
        assertEquals(Code.BAD_VALUE, EnumUtil.fromOrdinalOrNull(1, Code.class));
        assertEquals(Code.OK, EnumUtil.fromOrdinalOrNull(2, Code.class));
        assertNull(EnumUtil.fromOrdinalOrNull(3, Code.class));
        assertNull(EnumUtil.fromOrdinalOrNull(-1, Code.class));
        assertEquals(Code.OK, EnumUtil.fromOrdinalOr(-1, Code.class, Code.OK));
    }
}