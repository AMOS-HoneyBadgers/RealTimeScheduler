package com.honeybadgers.communication.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskQueueModel implements Serializable {

    private String id;

    private String groupId;

    private Map<String, String> metaData;

    private Timestamp dispatched;


    @Override
    public String toString() {
        return "TaskQueueModel{" +
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", metaData=" + metaData +
                ", dispatched=" + dispatched +
                '}';
    }
}
