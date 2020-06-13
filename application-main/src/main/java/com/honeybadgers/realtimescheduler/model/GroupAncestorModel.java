package com.honeybadgers.realtimescheduler.model;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;

@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupAncestorModel {

    @Column(name="id", unique = true, nullable = false)
    private String id;

    @Type(type = "string-array")
    @Column(name = "ancestors", columnDefinition = "character varying[]")
    private String[] ancestors;
}
