package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import com.honeybadgers.realtimescheduler.services.impl.SchedulerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SchedulerService.class)
public class SchedulerServiceTest {

    @MockBean
    private ITaskService taskService;

    @MockBean
    private TaskRedisRepository taskRedisRepository;

    @MockBean
    private LockRedisRepository lockRedisRepository;

    @MockBean

    private ICommunication sender;

    @Autowired
    private SchedulerService service;


    @Test
    public void testGetAllTasksAndSort() {
        // TODO IMPLEMENT @Christoff and @Stan
        service.getAllRedisTasksAndSort();
    }

    // TODO TEST ANPASSEN
    @Test(expected = RuntimeException.class)
    public void testScheduleTask() {
        Task t = new Task();
        t.setId("TEST");
        SchedulerService spy = spy(service);
        spy.scheduleTask(t.getId());
        //verify(taskRedisRepository).save(any());

    }

    @Test
    public void testCheckTaskOnLocked_NotLocked() {
        String taskId = UUID.randomUUID().toString();
        when(lockRedisRepository.findById(taskId)).thenReturn(Optional.of(taskId));

        assertTrue(service.checkTaskOnLocked(taskId));
    }

    @Test
    public void testCheckTaskOnLocked_Locked() {
        String taskId = UUID.randomUUID().toString();
        when(lockRedisRepository.findById(taskId)).thenReturn(Optional.empty());

        assertFalse(service.checkTaskOnLocked(taskId));
    }
}