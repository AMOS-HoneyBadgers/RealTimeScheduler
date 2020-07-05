package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.utils.IConvertUtils;
import org.springframework.stereotype.Service;

@Service
public class ConvertUtils implements IConvertUtils {
    /**
     * Converts the weekday so that it starts at zero.
     * Following syntax: Weekday: <Input from Calenday.DAY_OF_WEEK> --> <Output>
     * Monday: 2 --> 1
     * Tuesday: 3 --> 2
     * Wednesday: 4 --> 3
     * Thursday: 5 --> 4
     * Friday: 6 --> 5
     * Saturday: 7 --> 6
     * Sunday: 1 --> 0
     * @param dayofweek
     * @return
     */
    public int fitDayOfWeekToWorkingDayBooleans(int dayofweek) {
        if (dayofweek > 7 || dayofweek < 1)
            throw new IllegalArgumentException("Calendar.DAY_OF_WEEK should be between 1 and 7, but it is " + dayofweek);

        return dayofweek == 0 ? 6 : dayofweek - 1;
    }
}