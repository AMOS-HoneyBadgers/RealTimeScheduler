package com.honeybadgers.taskapi.models;

import com.honeybadgers.models.model.jpa.ActiveTimes;
import org.junit.jupiter.api.Test;

import java.sql.Time;

import static org.junit.jupiter.api.Assertions.*;

class TaskModelActiveTimesTest {

    @Test
    void to() {
        TaskModelActiveTimes model = new TaskModelActiveTimes();

        Time newValue = new Time(System.currentTimeMillis());
        TaskModelActiveTimes get = model.to(newValue);

        assertNotNull(get);
        assertEquals(get.getFrom(), model.getFrom());
        assertEquals(get.getTo(), newValue);
    }

    @Test
    void getsetTo() {
        TaskModelActiveTimes model = new TaskModelActiveTimes();

        Time newValue = new Time(System.currentTimeMillis());
        model.setTo(newValue);
        Time get = model.getTo();

        assertEquals(get, newValue);
    }

    @Test
    void from() {
        TaskModelActiveTimes model = new TaskModelActiveTimes();

        Time newValue = new Time(System.currentTimeMillis());
        TaskModelActiveTimes get = model.from(newValue);

        assertNotNull(get);
        assertEquals(get.getTo(), model.getTo());
        assertEquals(get.getFrom(), newValue);
    }

    @Test
    void getsetFrom() {
        TaskModelActiveTimes model = new TaskModelActiveTimes();

        Time newValue = new Time(System.currentTimeMillis());
        model.setFrom(newValue);
        Time get = model.getFrom();

        assertEquals(get, newValue);
    }

    @Test
    void testEquals() {
        TaskModelActiveTimes model1 = new TaskModelActiveTimes();
        TaskModelActiveTimes model2 = new TaskModelActiveTimes();
        TaskModelActiveTimes model3 = new TaskModelActiveTimes();
        model3.setTo(new Time(System.currentTimeMillis()));

        assertEquals(model1, model2);
        assertNotEquals(model1, model3);
        assertNotEquals(model2, model3);
    }

    @Test
    void testHashCode() {
        TaskModelActiveTimes model1 = new TaskModelActiveTimes();
        TaskModelActiveTimes model2 = new TaskModelActiveTimes();
        TaskModelActiveTimes model3 = new TaskModelActiveTimes();
        model3.setTo(new Time(System.currentTimeMillis()));

        assertEquals(model1.hashCode(), model2.hashCode());
        assertNotEquals(model1.hashCode(), model3.hashCode());
    }

    @Test
    void getAsJpaModel() {
        TaskModelActiveTimes model = new TaskModelActiveTimes();
        model.setTo(new Time(System.currentTimeMillis() + 1000));
        model.setFrom(new Time(System.currentTimeMillis()));

        ActiveTimes converted = model.getAsJpaModel();

        assertEquals(converted.getFrom(), model.getFrom());
        assertEquals(converted.getTo(), model.getTo());
    }
}