package com.honeybadgers.models.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockResponse {

    private String name;
    private String value;
    private String expires;
    private boolean expired;
}
