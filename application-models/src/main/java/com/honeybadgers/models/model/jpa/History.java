package com.honeybadgers.models.model.jpa;

import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class History {

    private String status;

    private Timestamp timestamp;

}
