package com.honeybadgers.realtimescheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveTimes {

    private Time from;

    private Time to;
}
