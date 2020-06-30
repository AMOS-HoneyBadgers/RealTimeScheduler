package com.honeybadgers.realtimescheduler.consumer;

import com.honeybadgers.models.model.Task;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.model.ModeEnum;
import com.honeybadgers.models.model.TaskStatusEnum;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.realtimescheduler.services.impl.SchedulerService;
import com.honeybadgers.realtimescheduler.services.impl.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@EnableRabbit
public class FeedbackConsumer {

    static final Logger logger = LogManager.getLogger(FeedbackConsumer.class);

    @Autowired
    SchedulerService schedulerService;

    @Autowired
    GroupRepository groupRepository;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Autowired
    TaskService taskService;



    // TODO WHEN TO DELETE THE TASK FROM POSTGRE DATABASE
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @RabbitListener(queues = "dispatch.feedback", containerFactory = "feedbackcontainerfactory")
    public void receiveFeedbackFromDispatcher(String taskid) throws InterruptedException {
        int iteration =1;
        while (true){
            try{
                logger.info("Task " + taskid + " was processed by the dispatcher");

                Task currentTask = taskService.getTaskById(taskid).orElse(null);
                if(currentTask == null)
                    throw new RuntimeException("could not find tasks in postgre database");

                checkAndSetParallelismDegree(currentTask);

                if(currentTask.getModeEnum()== ModeEnum.Sequential)
                    checkAndSetSequentialAndIndexNumber(currentTask);

                // Todo refactor put into 1 method
                taskService.updateTaskhistory(currentTask, TaskStatusEnum.Finished);
                taskService.finishTask(currentTask);

                schedulerService.scheduleTask("");
                break;
            }
            catch (LockAcquisitionException lockAcquisitionException){
                double timeToSleep= Math.random()*1000*iteration;
                logger.error("Task " + taskid + " couldn't acquire locks for setting its status to finished. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }


    }

    //TODO is it necessary to do this also for all grandparent groups??
    public void checkAndSetSequentialAndIndexNumber(Task currentTask) {
        Group group = currentTask.getGroup();
        logger.debug("Task " + currentTask.getId() + " update index Number of group " + group.getId());
        group.setLastIndexNumber(group.getLastIndexNumber()+1);
        groupRepository.save(group);
    }

    public void checkAndSetParallelismDegree(Task currentTask) {
        // Decrement current parallelismDegree in group of given task
        Optional<Group> updated = groupRepository.decrementCurrentParallelismDegree(currentTask.getGroup().getId());
        if(!updated.isPresent())
            logger.debug("Task " + currentTask.getId() + " failed to decrement currentParallelismDegree due to currentParallelismDegree = 0");
    }
}
