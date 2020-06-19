package com.honeybadgers.taskapi.service;

import com.honeybadgers.models.model.*;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.models.TaskModelActiveTimes;
import com.honeybadgers.taskapi.models.TaskModelMeta;
import com.honeybadgers.taskapi.service.impl.TaskConvertUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskConvertUtils.class)
public class TaskConvertUtilsTest {

    @Autowired
    ITaskConvertUtils converter;

    @Test
    public void testTaskJpaToRest(){

        Group group = new Group();
        group.setId("exampleGroup");

        Task exampleTask = new Task();
        String taskID = "70884515-8692-40e0-9c0e-e34ba4bcc3f8";
        int priority = 100;
        int indexNumber = 0;
        boolean force = false;
        ModeEnum mode = ModeEnum.Sequential;
        TypeFlagEnum type = TypeFlagEnum.Realtime;
        TaskStatusEnum status = TaskStatusEnum.Scheduled;
        int[] workdays = new int[]{1,0,1,0,1,0,1};
        Timestamp deadline = new Timestamp(123456789);
        int retries = 1;

        List<ActiveTimes> activeTimes = new LinkedList<ActiveTimes>();
        ActiveTimes timeFrame = new ActiveTimes();
        Time from = new Time(9,0,0);
        Time to = new Time(12,0,0);
        timeFrame.setFrom(from);
        timeFrame.setTo(to);
        activeTimes.add(timeFrame);

        Map<String, String> meta = new HashMap<String, String>();
        String key = "key123";
        String value = "value321";
        meta.put(key,value);

        exampleTask.setId(taskID);
        exampleTask.setGroup(group);
        exampleTask.setPriority(priority);
        exampleTask.setIndexNumber(indexNumber);
        exampleTask.setForce(force);
        exampleTask.setModeEnum(mode);
        exampleTask.setTypeFlagEnum(type);
        exampleTask.setStatus(status);
        exampleTask.setWorkingDays(workdays);
        exampleTask.setDeadline(deadline);
        exampleTask.setRetries(retries);
        exampleTask.setActiveTimeFrames(activeTimes);
        exampleTask.setMetaData(meta);

        TaskModel restModel = converter.taskJpaToRest(exampleTask);

        assertEquals(UUID.fromString(taskID),         restModel.getId());
        assertEquals(group.getId(),                   restModel.getGroupId());
        assertEquals(priority,                        restModel.getPriority().intValue());
        assertEquals(indexNumber,                     restModel.getIndexNumber().intValue());
        assertEquals(force,                           restModel.getForce());
        assertEquals(TaskModel.ModeEnum.SEQUENTIAL,   restModel.getMode());
        assertEquals(TaskModel.TypeFlagEnum.REALTIME, restModel.getTypeFlag());
        assertEquals(TaskModel.StatusEnum.SCHEDULED,  restModel.getStatus());
        assertEquals(workdays.length,                 restModel.getWorkingDays().size());
        assertEquals( deadline.getNanos(),            restModel.getDeadline().getNano());
        assertEquals(retries,                         restModel.getRetries().intValue());
        assertEquals(meta.get(key),                   restModel.getMeta().get(0).getValue());
        assertTrue( restModel.getWorkingDays().get(0) == true && restModel.getWorkingDays().get(1) == false );
        assertTrue( activeTimes.get(0).getFrom() ==  restModel.getActiveTimes().get(0).getFrom() &&
                    activeTimes.get(0).getTo() == restModel.getActiveTimes().get(0).getTo());
    }

    @Test
    public void testActiveTimesJpaToRest(){
        List<ActiveTimes> times = new LinkedList<ActiveTimes>();
        for(int i = 0; i < 5; i++){
            Time from = new Time(5+i,0,0);
            Time to = new Time(6+i,0,0);
            ActiveTimes timeFrame = new ActiveTimes();
            timeFrame.setFrom(from);
            timeFrame.setTo(to);
            times.add(timeFrame);
        }

        List<TaskModelActiveTimes> res = converter.activeTimesJpaToRest(times);

        assertEquals(5, res.size() );
        assertEquals(5, res.get(0).getFrom().getHours());
    }

    @Test
    public void testMetaDataJpaToRest(){

        Map<String, String> metaMap = new HashMap<String,String>();
        metaMap.put("key1", "value1");
        metaMap.put("key2", "value2");
        metaMap.put("key3", "value3");

        List<TaskModelMeta> res = converter.metaDataJpaToRest(metaMap);

        assertEquals(3, res.size());
       assertEquals("value1", res.get(0).getValue());
    }
}
