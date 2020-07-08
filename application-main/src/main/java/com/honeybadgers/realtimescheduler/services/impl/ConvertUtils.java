package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.utils.IConvertUtils;
import org.springframework.stereotype.Service;

@Service
public class ConvertUtils implements IConvertUtils {
    /**
     * Converts the weekday so that it starts at zero.
     * Following syntax: Weekday: <Input from Calendar.DAY_OF_WEEK> --> <Output>
     * Monday: 2 --> 0
     * Tuesday: 3 --> 1
     * Wednesday: 4 --> 2
     * Thursday: 5 --> 3
     * Friday: 6 --> 4
     * Saturday: 7 --> 5
     * Sunday: 1 --> 6
     * @param dayofweek equals Calendar.DAY_OF_WEEK
     * @return the weekday with monday equals zero
     */
    public int fitDayOfWeekToWorkingDayBooleans(int dayofweek) {
        if (dayofweek > 7 || dayofweek < 1)
            throw new IllegalArgumentException("Calendar.DAY_OF_WEEK should be between 1 and 7, but it is " + dayofweek);

        return dayofweek == 1 ? 6 : dayofweek - 2;
    }
}