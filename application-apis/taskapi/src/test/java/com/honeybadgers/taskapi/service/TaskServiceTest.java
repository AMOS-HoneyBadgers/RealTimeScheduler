package com.honeybadgers.taskapi.service;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.*;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import com.honeybadgers.taskapi.exceptions.CreationException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.models.TaskModelMeta;
import com.honeybadgers.taskapi.service.impl.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskService.class)
public class TaskServiceTest {

    @MockBean
    TaskRepository taskRepository;
    @MockBean
    GroupRepository groupRepository;
    @MockBean
    ICommunication communication;
    @MockBean
    ITaskConvertUtils converter;

    @Autowired
    ITaskService taskService;


    @Before
    public void setUp() {
        Group group = new Group();
        group.setId("testGroup");

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
    }

    @Test
    public void testGetAllTasksAsRestModel(){
        List<Task> tasksList = new LinkedList<Task>();
        tasksList.add(generateFullTask(0));
        tasksList.add(generateFullTask(1));
        tasksList.add(generateFullTask(2));

        when(taskRepository.findAll()).thenReturn(tasksList);
        List<TaskModel> restModelList = taskService.getAllTasks();

        assertNotNull(restModelList);
        assertEquals(3, restModelList.size());
    }


    @Test
    public void testCreateTask() throws JpaException, UnknownEnumException, CreationException {
        UUID taskId = UUID.randomUUID();

        TaskModel restModel = new TaskModel();
        restModel.setId(taskId);

        Task createdTask = new Task();
        createdTask.setId(taskId.toString());

        when(converter.taskRestToJpa(restModel)).thenReturn(createdTask);
        Task t = taskService.createTask(restModel);

        assertNotNull(t);
        assertThat(t.getId()).isEqualTo(restModel.getId().toString());
    }

    @Test
    public void testCreateTask_JpaException() {
        JpaException vio = new JpaException("Primary or unique constraint failed!");

        Task task = new Task();
        task.setId(UUID.randomUUID().toString());

        when(taskRepository.findById(any())).thenReturn(Optional.of(task));

        TaskModel restModel = new TaskModel();
        restModel.setId(UUID.randomUUID());
        restModel.setGroupId("testGroup");

        Exception e = assertThrows(JpaException.class, () -> taskService.createTask(restModel));
        assertEquals("Primary or unique constraint failed!", e.getMessage());
    }

    @Test
    public void testUpdateTask() throws UnknownEnumException, JpaException, CreationException {
        UUID taskId =  UUID.randomUUID();

        TaskModel restModel = new TaskModel();
        restModel.setId(taskId);
        restModel.setPriority(100);

        Task updatedTask = new Task();
        updatedTask.setId(taskId.toString());
        updatedTask.setPriority(5);

        when(taskRepository.findById(any())).thenReturn(Optional.of(updatedTask));
        when(converter.taskRestToJpa(restModel)).thenReturn(updatedTask);

        Task t = taskService.updateTask(taskId, restModel);

        verify(communication,Mockito.only()).sendTaskToDispatcher(Mockito.any());
        assertNotNull(t);
        assertEquals(updatedTask.getPriority(),t.getPriority());
    }

    @Test
    public void testUpdateTask_Exeption() throws UnknownEnumException, JpaException, CreationException {
        UUID taskId =  UUID.randomUUID();

        TaskModel restModel = new TaskModel();
        restModel.setId(taskId);
        restModel.setPriority(100);

        when(taskRepository.findById(taskId.toString())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> taskService.updateTask(taskId, restModel));
    }

    @Test
    public void testUpdateTask_forceExeption() throws UnknownEnumException, JpaException, CreationException {
        UUID taskId =  UUID.randomUUID();

        Task task = new Task();
        task.setId(taskId.toString());
        task.setForce(true);

        TaskModel restModel = new TaskModel();
        restModel.setId(taskId);
        restModel.setPriority(100);
        restModel.setForce(false);

        when(taskRepository.findById(taskId.toString())).thenReturn(Optional.of(task));

        assertThrows(IllegalStateException.class, () -> taskService.updateTask(taskId, restModel));
    }

    @Test
    public void testCreateTask_primaryKeyViolation() {
        Task alreadyInTask = new Task();
        alreadyInTask.setId("AlreadyExistingUUID");

        when(taskRepository.findById(any(String.class))).thenReturn(Optional.of(alreadyInTask));

        TaskModel restModel = new TaskModel();
        restModel.setId(UUID.randomUUID());
        restModel.setGroupId("testGroup");

        Exception e = assertThrows(JpaException.class, () -> taskService.createTask(restModel));
        assertEquals("Primary or unique constraint failed!", e.getMessage());
    }

    @Test
    public void getTaskById(){
        Task task = generateFullTask(0);
        TaskModel taskmodel = new TaskModel();
        taskmodel.setId(UUID.fromString(task.getId()));

        when(taskRepository.findById(anyString())).thenReturn(Optional.of(task));
        when(converter.taskJpaToRest(task)).thenReturn(taskmodel);

        TaskModel requestedTask = taskService.deleteTask(UUID.fromString(task.getId()));

        assertNotNull(requestedTask);
        assertEquals(task.getId(), requestedTask.getId().toString());
    }

    @Test
    public void getNonExistingTaskByI(){
        UUID id = UUID.randomUUID();

        when(taskRepository.findById(anyString())).thenReturn(Optional.empty());

        Exception  e = assertThrows(NoSuchElementException.class, () ->taskService.deleteTask(id));
        assertNotNull(e);
        assertEquals("No existing Task with ID: " + id, e.getMessage());
    }

    @Test
    public void testDeleteTask(){
        Task task = generateFullTask(0);
        TaskModel taskmodel = new TaskModel();
        taskmodel.setId(UUID.fromString(task.getId()));

        when(taskRepository.findById(anyString())).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).deleteById(anyString());
        when(converter.taskJpaToRest(task)).thenReturn(taskmodel);

        TaskModel deletedTask = taskService.deleteTask(UUID.fromString(task.getId()));

        assertNotNull(deletedTask);
        assertEquals(task.getId(), deletedTask.getId().toString());
    }

    @Test
    public void testDeleteNonExistingTask(){
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(anyString())).thenReturn(Optional.empty());

        Exception  e = assertThrows(NoSuchElementException.class, () ->taskService.deleteTask(id));
        assertNotNull(e);
        assertEquals("No existing Task with ID: " + id, e.getMessage());
        verify(taskRepository, Mockito.never()).deleteById(anyString());
    }

    @Test
    public void testSendTaskToPriorityQueue() {
        TaskModel taskModel = new TaskModel();
        taskModel.setId(UUID.randomUUID());
        taskModel.setGroupId("testgroup");
        taskModel.setDeadline(OffsetDateTime.MAX);
        TaskModelMeta meta = new TaskModelMeta();
        meta.setKey("test");
        meta.setValue("tset");
        taskModel.setMeta(Collections.singletonList(meta));
        taskModel.setPriority(1);
        taskModel.setIndexNumber(1);
        taskModel.setRetries(9);
        taskModel.setTypeFlag(TaskModel.TypeFlagEnum.BATCH);
        taskService.sendTaskToPriorityQueue(taskModel);
        verify(communication,Mockito.only()).sendTaskToPriorityQueue(Mockito.any());
    }

    @Test
    public void testSendTaskToPriorityQueue_NullpointerCheck() {
        TaskModel taskModel = new TaskModel();
        taskModel.setId(UUID.randomUUID());
        taskModel.setPriority(1);
        taskService.sendTaskToPriorityQueue(taskModel);
        verify(communication,Mockito.only()).sendTaskToPriorityQueue(Mockito.any());
    }


    private Task generateFullTask(int diff){
        Group group = new Group();
        group.setId("exampleGroup");

        Task exampleTask = new Task();

        String taskID = "70884515-8692-40e0-9c0e-e34ba4bcc3f" + Integer.toString(diff);
        int priority = 100 + diff;
        int indexNumber = 0;
        int retries = 1;
        int[] workdays = new int[]{1,0,1,0,1,0,1};
        boolean force = false;
        ModeEnum mode = ModeEnum.Sequential;
        TypeFlagEnum type = TypeFlagEnum.Realtime;
        TaskStatusEnum status = TaskStatusEnum.Scheduled;
        Timestamp deadline = new Timestamp(123456789);

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

        return exampleTask;
    }

    @Test
    public void testCreateTask_Exeption() throws UnknownEnumException, JpaException, CreationException {
        UUID taskId = UUID.randomUUID();

        TaskModel restModel = new TaskModel();
        restModel.setId(taskId);

        Task createdTask = new Task();
        createdTask.setId(taskId.toString());

        when(converter.taskRestToJpa(restModel)).thenReturn(createdTask);
        when(taskRepository.save(createdTask)).thenThrow(new DataIntegrityViolationException(""));

       assertThrows(JpaException.class, () -> taskService.createTask(restModel));
    }

    @Test
    public void testGetTaskById(){
        UUID id = UUID.randomUUID();
        Task task = new Task();
        task.setId(id.toString());

        when(taskRepository.findById(id.toString())).thenReturn(Optional.of(task));
        taskService.getTaskById(id);

        verify(converter).taskJpaToRest(task);
    }

    @Test
    public void testGetTaskById_Exeption(){
        UUID id = UUID.randomUUID();

        assertThrows(NoSuchElementException.class, () -> taskService.getTaskById(id));
    }

    @Test
    public void testupdateTask_Exeption() throws UnknownEnumException, JpaException, CreationException {
        UUID taskId = UUID.randomUUID();
        TaskModel restModel = new TaskModel();
        restModel.setId(taskId);

        Task createdTask = new Task();
        createdTask.setId(taskId.toString());

        when(converter.taskRestToJpa(restModel)).thenReturn(createdTask);
        when(taskRepository.save(createdTask)).thenThrow(new DataIntegrityViolationException(""));
        when(taskRepository.findById(taskId.toString())).thenReturn(Optional.of(new Task()));

        assertThrows(JpaException.class, () -> taskService.updateTask(taskId, restModel));
    }
}
