package com.honeybadgers.models.utils;

import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IConvertUtilsTest {

    TestConvertUtils testConvertUtils = new TestConvertUtils();

    @Test
    public void testTimestampJpaToRest() {
        OffsetDateTime dateTime;
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        dateTime = testConvertUtils.timestampJpaToRest(timestamp);

        assertNotNull(dateTime);
        // OffsetDateTime does not have method to get milliseconds since epoch -> getSecondsSinceEpoch + getNanosOfCurrentSecond concatenated
        long dateTimeAsMilli = Long.parseLong(Long.toString(dateTime.toEpochSecond()) + Long.toString(dateTime.getNano()).substring(0, 3));
        assertEquals(timestamp.getTime(), dateTimeAsMilli);
   }

    @Test
    public void testTimestampJpaToRest_nullInput() {
        OffsetDateTime dateTime;
        Timestamp timestamp = null;

        dateTime = testConvertUtils.timestampJpaToRest(timestamp);

        assertNull(dateTime);
    }

    @Test
    public void testTimestampRestToJpa() {
        OffsetDateTime dateTime = OffsetDateTime.now();
        Timestamp timestamp;

        timestamp = testConvertUtils.timestampRestToJpa(dateTime);

        // OffsetDateTime does not have method to get milliseconds since epoch -> getSecondsSinceEpoch + getNanosOfCurrentSecond concatenated
        long dateTimeAsMilli = Long.parseLong(Long.toString(dateTime.toEpochSecond()) + Long.toString(dateTime.getNano()).substring(0, 3));
        assertNotNull(timestamp);
        assertEquals(timestamp.getTime(), dateTimeAsMilli);
    }

    @Test
    public void testTimestampRestToJpa_nullInput() {
        OffsetDateTime dateTime = null;
        Timestamp timestamp;

        timestamp = testConvertUtils.timestampRestToJpa(dateTime);

        assertNull(timestamp);
    }

    @Test
    public void testBoolListToIntArray() {
        List<Boolean> bools = Arrays.asList(false, false, true, true);
        int[] ints;

        ints = testConvertUtils.boolListToIntArray(bools);

        assertNotNull(ints);
        assertArrayEquals(new int[] {0,0,1,1}, ints);
    }

    @Test
    public void testBoolListToIntArray_nullInput() {
        List<Boolean> bools = null;
        int[] ints;

        ints = testConvertUtils.boolListToIntArray(bools);

        assertNotNull(ints);
        assertArrayEquals(new int[] {1, 1, 1, 1, 1, 1, 1}, ints);
    }

    @Test
    public void testIntArrayToBoolList() {
        List<Boolean> bools;
        int[] ints = new int[] {1, 0, 0, 1, 1, 0};

        bools = testConvertUtils.intArrayToBoolList(ints);

        assertNotNull(bools);
        assertArrayEquals(new Boolean[] {true, false, false, true, true, false}, bools.toArray());
    }

    @Test
    public void testIntArrayToBoolList_nullInput() {
        List<Boolean> bools;
        int[] ints = null;

        bools = testConvertUtils.intArrayToBoolList(ints);

        assertNotNull(bools);
        assertArrayEquals(new Boolean[] {true, true, true, true, true, true, true}, bools.toArray());
    }
}
