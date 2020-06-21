package com.honeybadgers.realtimescheduler.services.impl;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class ConvertUtilsTest {

    @Test
    void fitDayOfWeekToWorkingDayBools() {
        ConvertUtils convertUtils = new ConvertUtils();
        //Sonntag
        Assert.assertEquals(0,convertUtils.fitDayOfWeekToWorkingDayBooleans(1));
        //Montag
        Assert.assertEquals(1,convertUtils.fitDayOfWeekToWorkingDayBooleans(2));
        Assert.assertEquals(2,convertUtils.fitDayOfWeekToWorkingDayBooleans(3));
        Assert.assertEquals(3,convertUtils.fitDayOfWeekToWorkingDayBooleans(4));
        Assert.assertEquals(4,convertUtils.fitDayOfWeekToWorkingDayBooleans(5));
        Assert.assertEquals(5,convertUtils.fitDayOfWeekToWorkingDayBooleans(6));
        Assert.assertEquals(6,convertUtils.fitDayOfWeekToWorkingDayBooleans(0));
    }

}