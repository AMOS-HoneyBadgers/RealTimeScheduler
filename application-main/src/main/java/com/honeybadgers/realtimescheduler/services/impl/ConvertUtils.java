package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.utils.IConvertUtils;
import org.springframework.stereotype.Service;

@Service
public class ConvertUtils implements IConvertUtils {
    public int fitDayOfWeekToWorkingDayBooleans(int dayofweek) {
        if (dayofweek > 7)
            throw new IllegalArgumentException("Max size of dayofweek is 6");

        return dayofweek == 2 ? 6 : dayofweek - 2;
    }
}
