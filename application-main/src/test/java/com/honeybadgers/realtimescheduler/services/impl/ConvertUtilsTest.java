package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.models.model.jpa.Group;
import com.honeybadgers.models.model.jpa.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConvertUtilsTest {

    // No need for springboot test -> initialize normally (due to no Autowires etc in ConvertUtils)
    ConvertUtils convertUtils = new ConvertUtils();

    @Test
    public void fitDayOfWeekToWorkingDayBools() {
        //Sonntag
        assertEquals(6, convertUtils.fitDayOfWeekToWorkingDayBooleans(1));
        //Montag
        assertEquals(0, convertUtils.fitDayOfWeekToWorkingDayBooleans(2));
        assertEquals(1, convertUtils.fitDayOfWeekToWorkingDayBooleans(3));
        assertEquals(2, convertUtils.fitDayOfWeekToWorkingDayBooleans(4));
        assertEquals(3, convertUtils.fitDayOfWeekToWorkingDayBooleans(5));
        assertEquals(4, convertUtils.fitDayOfWeekToWorkingDayBooleans(6));
        assertEquals(5, convertUtils.fitDayOfWeekToWorkingDayBooleans(7));
        assertThrows(IllegalArgumentException.class, () -> convertUtils.fitDayOfWeekToWorkingDayBooleans(0));
        assertThrows(IllegalArgumentException.class, () -> convertUtils.fitDayOfWeekToWorkingDayBooleans(8));
    }

    @Test
    public void testTaskJpaToQueue() {
        Group gr = new Group();
        gr.setId("TEST");

        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setGroup(gr);

        TaskQueueModel res = convertUtils.taskJpaToQueue(task);

        assertNotNull(res);
        assertEquals(task.getId(), res.getId());
        assertEquals(task.getGroup().getId(), res.getGroupId());
        assertNotNull(res.getDispatched());
        assertNull(res.getMetaData());
    }

    @Test
    public void testTaskJpaToQueue_withMeta() {
        Group gr = new Group();
        gr.setId("TEST");

        Map<String, String> meta = new HashMap<>();
        meta.put("TEST", "TEST");

        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setGroup(gr);
        task.setMetaData(meta);

        TaskQueueModel res = convertUtils.taskJpaToQueue(task);

        assertNotNull(res);
        assertEquals(task.getId(), res.getId());
        assertEquals(task.getGroup().getId(), res.getGroupId());
        assertNotNull(res.getDispatched());
        assertNotNull(res.getMetaData());
        assertTrue(res.getMetaData().containsKey("TEST"));
    }

    @Test(expected = IllegalStateException.class)
    public void testTaskJpaToQueue_IllegalStateExc() {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());

        convertUtils.taskJpaToQueue(task);
    }

}