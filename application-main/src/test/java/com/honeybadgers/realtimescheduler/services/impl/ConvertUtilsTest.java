package com.honeybadgers.realtimescheduler.services.impl;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConvertUtilsTest {

    @Test
    public void fitDayOfWeekToWorkingDayBools() {
        ConvertUtils convertUtils = new ConvertUtils();
        //Sonntag
        assertEquals(6,convertUtils.fitDayOfWeekToWorkingDayBooleans(1));
        //Montag
        assertEquals(0,convertUtils.fitDayOfWeekToWorkingDayBooleans(2));
        assertEquals(1,convertUtils.fitDayOfWeekToWorkingDayBooleans(3));
        assertEquals(2,convertUtils.fitDayOfWeekToWorkingDayBooleans(4));
        assertEquals(3,convertUtils.fitDayOfWeekToWorkingDayBooleans(5));
        assertEquals(4,convertUtils.fitDayOfWeekToWorkingDayBooleans(6));
        assertEquals(5,convertUtils.fitDayOfWeekToWorkingDayBooleans(7));
        assertThrows(IllegalArgumentException.class, () -> convertUtils.fitDayOfWeekToWorkingDayBooleans(0));
        assertThrows(IllegalArgumentException.class, () -> convertUtils.fitDayOfWeekToWorkingDayBooleans(8));
    }

}