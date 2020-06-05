package com.honeybadgers.communication.model;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskQueueModel implements Serializable {
    @Override
    public String toString() {
        return "TaskQueueModel{" +
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", priority=" + priority +
                ", deadline=" + deadline +
                ", typeFlagEnum='" + typeFlagEnum + '\'' +
                ", retries=" + retries +
                ", indexNumber=" + indexNumber +
                ", metaData=" + metaData +
                '}';
    }

    private String id;

    private String groupId;

    @Max(value = 999)
    @Min(value = 0)
    private int priority;

    private Timestamp deadline;

    private String typeFlagEnum;

    @Min(value = 0)
    private int retries = 0;

    @Min(value = 1)
    private Integer indexNumber;

    private Map<String, String> metaData;

}
