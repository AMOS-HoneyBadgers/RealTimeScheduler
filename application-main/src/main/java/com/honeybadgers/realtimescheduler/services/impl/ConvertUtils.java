package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.utils.IConvertUtils;
import org.springframework.stereotype.Service;

@Service
public class ConvertUtils implements IConvertUtils {
    public int fitDayOfWeekToWorkingDayBooleans(int dayofweek) {
        if (dayofweek > 7 || dayofweek < 1)
            throw new IllegalArgumentException("Calendar.DAY_OF_WEEK should be between 1 and 7, but it is " + dayofweek);

        return dayofweek == 0 ? 6 : dayofweek - 1;
    }
}