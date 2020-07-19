package com.honeybadgers.models.model.jpa;

import lombok.*;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ActiveTimes {

    private Time from;

    private Time to;
}
