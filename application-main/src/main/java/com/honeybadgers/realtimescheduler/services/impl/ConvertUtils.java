package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.utils.IConvertUtils;

public class ConvertUtils implements IConvertUtils {
    public int fitDayOfWeekToWorkingDayBooleans(int dayofweek) {
        if (dayofweek > 6)
            throw new IllegalArgumentException("Max size of dayofweek is 6");

        return dayofweek == 0 ? 6 : dayofweek - 1;
    }
}
