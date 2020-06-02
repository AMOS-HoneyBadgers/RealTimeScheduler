package com.honeybadgers.realtimescheduler.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RedisTaskTest {

    RedisTask task;

    @BeforeEach
    public void resetTask() {
        task = new RedisTask();
    }

    @Test
    public void testSetterGetterId() {

        assertNull(task.getId());

        String newId = "newId";
        task.setId(newId);
        String get = task.getId();

        assertEquals(get, newId);
    }

    @Test
    public void testSetterGetterPrio() {

        assertEquals(0, task.getPriority());

        long newPrio = 546545;
        task.setPriority(newPrio);
        long get = task.getPriority();

        assertEquals(get, newPrio);
    }
}
