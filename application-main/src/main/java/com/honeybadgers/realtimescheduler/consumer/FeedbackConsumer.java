package com.honeybadgers.realtimescheduler.consumer;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.Task;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.model.ModeEnum;
import com.honeybadgers.models.model.TaskStatusEnum;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.realtimescheduler.services.impl.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@EnableRabbit
public class FeedbackConsumer {

    static final Logger logger = LogManager.getLogger(FeedbackConsumer.class);

    @Autowired
    public ICommunication sender;

    @Autowired
    GroupRepository groupRepository;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Autowired
    TaskService service;


    // TODO Transaction
    // TODO HANDLE NEGATIVE FEEDBACK
    // TODO WHEN TO DELETE THE TASK FROM POSTGRE DATABASE
    @RabbitListener(queues = "dispatch.feedback", containerFactory = "feedbackcontainerfactory")
    public void receiveFeedbackFromDispatcher(String id) throws InterruptedException {
        logger.info("Task " + id + " was processed by the dispatcher");

        Task currentTask = service.getTaskById(id).orElse(null);
        if(currentTask == null)
            throw new RuntimeException("could not find tasks in postgre database");

        checkAndSetParallelismDegree(currentTask);

        if(currentTask.getModeEnum()== ModeEnum.Sequential)
            checkAndSetSequentialAndIndexNumber(currentTask);

        currentTask.setStatus(TaskStatusEnum.Finished);
        service.uploadTask(currentTask);

        sender.sendTaskToTasksQueue(scheduler_trigger);
    }

    //TODO is it necessary to do this also for all grandparent groups??
    public void checkAndSetSequentialAndIndexNumber(Task currentTask) {
        Group group = currentTask.getGroup();
        logger.debug("Task " + currentTask.getId() + " update index Number of group " + group.getId());
        group.setLastIndexNumber(group.getLastIndexNumber()+1);
        groupRepository.save(group);
    }

    public void checkAndSetParallelismDegree(Task currentTask) {
        // Get Current Running Tasks from Redis Database, throw exception if it wasnt found
        Optional<Group> updated = groupRepository.decrementCurrentParallelismDegree(currentTask.getGroup().getId());
        if(!updated.isPresent())
            logger.debug("Task " + currentTask.getId() + " failed to decrement currentParallelismDegree due to currentParallelismDegree = 0");
    }
}
