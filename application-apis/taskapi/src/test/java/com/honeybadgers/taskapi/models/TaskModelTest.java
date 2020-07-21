package com.honeybadgers.taskapi.models;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TaskModelTest {
    @Test
    void id() {
        TaskModel model = new TaskModel();

        String newValue = UUID.randomUUID().toString();
        TaskModel get = model.id(newValue);

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getId(), newValue);
    }

    @Test
    void getsetId() {
        TaskModel model = new TaskModel();

        String newValue = UUID.randomUUID().toString();
        model.setId(newValue);
        String get = model.getId();

        assertEquals(get, newValue);
    }

    @Test
    void groupId() {
        TaskModel model = new TaskModel();

        String newValue = "TestGroupId";
        TaskModel get = model.groupId(newValue);

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getGroupId(), newValue);
    }

    @Test
    void getsetGroupId() {
        TaskModel model = new TaskModel();

        String newValue = "NewGroupId";
        model.setGroupId(newValue);
        String get = model.getGroupId();

        assertEquals(get, newValue);
    }

    @Test
    void priority() {
        TaskModel model = new TaskModel();

        Integer newValue = 2;
        TaskModel get = model.priority(newValue);

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getPriority(), newValue);
    }

    @Test
    void getsetPriority() {
        TaskModel model = new TaskModel();

        Integer newValue = 2;
        model.setPriority(newValue);
        Integer get = model.getPriority();

        assertEquals(get, newValue);
    }

    @Test
    void deadline() {
        TaskModel model = new TaskModel();

        OffsetDateTime newValue = OffsetDateTime.now();
        TaskModel get = model.deadline(newValue);

        assertNotNull(get);
        assertEquals(get.getId(), model.getId());
        assertEquals(get.getDeadline(), newValue);
    }

    @Test
    void getsetDeadline() {
        TaskModel model = new TaskModel();

        OffsetDateTime newValue = OffsetDateTime.now();
        model.setDeadline(newValue);
        OffsetDateTime get = model.getDeadline();

        assertEquals(get, newValue);
    }

    @Test
    void activeTimes() {
        TaskModel model = new TaskModel();

        TaskModelActiveTimes newValue = new TaskModelActiveTimes();
        TaskModel get = model.activeTimes(Collections.singletonList(newValue));

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getActiveTimes(), Collections.singletonList(newValue));
    }

    @Test
    void addActiveTimesItem() {
    }

    @Test
    void getsetActiveTimes() {
        TaskModel model = new TaskModel();

        List<TaskModelActiveTimes> newValue = Collections.singletonList(new TaskModelActiveTimes());
        model.setActiveTimes(newValue);
        List<TaskModelActiveTimes> get = model.getActiveTimes();

        assertArrayEquals(get.toArray(), newValue.toArray());
    }

    @Test
    void workingDays() {
        TaskModel model = new TaskModel();

        List<Boolean> newValue = Arrays.asList(true, true, true, true, true, true, true);
        TaskModel get = model.workingDays(newValue);

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getWorkingDays(), newValue);
        assertArrayEquals(get.getWorkingDays().toArray(), newValue.toArray());
    }

    @Test
    void addWorkingDaysItem() {
    }

    @Test
    void getsetWorkingDays() {
        TaskModel model = new TaskModel();

        List<Boolean> newValue = Arrays.asList(true, true, true, true, true, true, true);
        model.setWorkingDays(newValue);
        List<Boolean> get = model.getWorkingDays();

        assertArrayEquals(get.toArray(), newValue.toArray());
    }

    @Test
    void status() {
        TaskModel model = new TaskModel();

        TaskModel.StatusEnum newValue = TaskModel.StatusEnum.DISPATCHED;
        TaskModel get = model.status(newValue);

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getStatus(), newValue);
    }

    @Test
    void getsetStatus() {
        TaskModel model = new TaskModel();

        TaskModel.StatusEnum newValue = TaskModel.StatusEnum.FINISHED;
        model.setStatus(newValue);
        TaskModel.StatusEnum get = model.getStatus();

        assertEquals(get, newValue);
    }

    @Test
    void typeFlag() {
        TaskModel model = new TaskModel();

        TaskModel.TypeFlagEnum newValue = TaskModel.TypeFlagEnum.BATCH;
        TaskModel get = model.typeFlag(newValue);

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getTypeFlag(), newValue);
    }

    @Test
    void getsetTypeFlag() {
        TaskModel model = new TaskModel();

        TaskModel.TypeFlagEnum newValue = TaskModel.TypeFlagEnum.REALTIME;
        model.setTypeFlag(newValue);
        TaskModel.TypeFlagEnum get = model.getTypeFlag();

        assertEquals(get, newValue);
    }

    @Test
    void mode() {
        TaskModel model = new TaskModel();

        TaskModel.ModeEnum newValue = TaskModel.ModeEnum.PARALLEL;
        TaskModel get = model.mode(newValue);

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getMode(), newValue);
    }

    @Test
    void getsetMode() {
        TaskModel model = new TaskModel();

        TaskModel.ModeEnum newValue = TaskModel.ModeEnum.SEQUENTIAL;
        model.setMode(newValue);
        TaskModel.ModeEnum get = model.getMode();

        assertEquals(get, newValue);
    }

    @Test
    void retries() {
        TaskModel model = new TaskModel();

        Integer newValue = 2;
        TaskModel get = model.retries(newValue);

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getRetries(), newValue);
    }

    @Test
    void getsetRetries() {
        TaskModel model = new TaskModel();

        Integer newValue = 3;
        model.setRetries(newValue);
        Integer get = model.getRetries();

        assertEquals(get, newValue);
    }

    @Test
    void force() {
        TaskModel model = new TaskModel();

        Boolean newValue = false;
        TaskModel get = model.force(newValue);

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getForce(), newValue);
    }

    @Test
    void getsetForce() {
        TaskModel model = new TaskModel();

        Boolean newValue = true;
        model.setForce(newValue);
        Boolean get = model.getForce();

        assertEquals(get, newValue);
    }

    @Test
    void indexNumber() {
        TaskModel model = new TaskModel();

        Integer newValue = 202;
        TaskModel get = model.indexNumber(newValue);

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getIndexNumber(), newValue);
    }

    @Test
    void getsetIndexNumber() {
        TaskModel model = new TaskModel();

        Integer newValue = 2;
        model.setIndexNumber(newValue);
        Integer get = model.getIndexNumber();

        assertEquals(get, newValue);
    }

    @Test
    void meta() {
        TaskModel model = new TaskModel();

        List<TaskModelMeta> newValue = Collections.singletonList(new TaskModelMeta());
        TaskModel get = model.meta(newValue);

        assertNotNull(get);
        assertEquals(get.getDeadline(), model.getDeadline());
        assertEquals(get.getMeta(), newValue);
        assertArrayEquals(get.getMeta().toArray(), newValue.toArray());
    }

    @Test
    void addMetaItem() {
    }

    @Test
    void getsetMeta() {
        TaskModel model = new TaskModel();

        List<TaskModelMeta> newValue = Collections.singletonList(new TaskModelMeta());
        model.setMeta(newValue);
        List<TaskModelMeta> get = model.getMeta();

        assertEquals(get, newValue);
        assertArrayEquals(get.toArray(), newValue.toArray());
    }

    @Test
    void testEquals() {
        TaskModel model1 = new TaskModel();
        TaskModel model2 = new TaskModel();
        TaskModel model3 = new TaskModel();
        model3.setId(UUID.randomUUID().toString());

        assertEquals(model1, model2);
        assertNotEquals(model1, model3);
        assertNotEquals(model2, model3);
    }

    @Test
    void testHashCode() {
        TaskModel model1 = new TaskModel();
        TaskModel model2 = new TaskModel();
        TaskModel model3 = new TaskModel();
        model3.setId(UUID.randomUUID().toString());

        assertEquals(model1.hashCode(), model2.hashCode());
        assertNotEquals(model1.hashCode(), model3.hashCode());
    }
}
