package com.honeybadgers.models.model;

import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LockResponse {

    private String name;
    private String value;
    private String expires;
    private boolean expired;
}
