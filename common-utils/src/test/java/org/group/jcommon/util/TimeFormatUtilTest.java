package org.group.jcommon.util;

import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TimeFormatUtilTest {

    @Test
    public void testParseTime() {
        String ts = "2017-02-10 10:00:00";
        long cst = TimeFormatUtil.mustParseCST(ts);
        long utc = TimeFormatUtil.mustParseUTC(ts);
        assertEquals(TimeUnit.HOURS.toMillis(8), utc - cst);
    }

    @Test
    public void testFormatTime() {
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String ts = "2017-02-16 23:50:00";
        long utc = TimeFormatUtil.mustParseUTC(ts);
        assertEquals("2017-02-16", TimeFormatUtil.timeInUTC(utc, DATE_FORMATTER));
    }

    @Test
    public void testParseMalformed() {
        assertEquals(-2L, TimeFormatUtil.parseUTC("crap").or(-2L).longValue());
    }

    @Test
    public void testMustParse() {
        assertThrows(IllegalArgumentException.class, () -> TimeFormatUtil.mustParseUTC("crap"));
    }
}