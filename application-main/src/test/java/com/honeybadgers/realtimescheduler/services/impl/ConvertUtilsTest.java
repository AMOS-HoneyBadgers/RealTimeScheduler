package com.honeybadgers.realtimescheduler.services.impl;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;

class ConvertUtilsTest {

    @Test
    void fitDayOfWeekToWorkingDayBools() {
        ConvertUtils convertUtils = new ConvertUtils();
        //Sonntag
        Assert.assertEquals(0,convertUtils.fitDayOfWeekToWorkingDayBools(1));
        //Montag
        Assert.assertEquals(1,convertUtils.fitDayOfWeekToWorkingDayBools(2));
        Assert.assertEquals(2,convertUtils.fitDayOfWeekToWorkingDayBools(3));
        Assert.assertEquals(3,convertUtils.fitDayOfWeekToWorkingDayBools(4));
        Assert.assertEquals(4,convertUtils.fitDayOfWeekToWorkingDayBools(5));
        Assert.assertEquals(5,convertUtils.fitDayOfWeekToWorkingDayBools(6));
        Assert.assertEquals(6,convertUtils.fitDayOfWeekToWorkingDayBools(0));
    }

}