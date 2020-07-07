package com.honeybadgers.realtimescheduler.consumer.impl;

import com.honeybadgers.models.model.Task;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.model.ModeEnum;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.realtimescheduler.consumer.IFeedbackConsumer;
import com.honeybadgers.realtimescheduler.services.impl.SchedulerService;
import com.honeybadgers.realtimescheduler.services.impl.TaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.TransactionException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@EnableRabbit
public class FeedbackConsumer implements IFeedbackConsumer {

    static final Logger logger = LogManager.getLogger(FeedbackConsumer.class);

    @Autowired
    FeedbackConsumer _self;

    @Autowired
    SchedulerService schedulerService;

    @Autowired
    GroupRepository groupRepository;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Autowired
    TaskService taskService;

    @Value("${com.honeybadgers.transaction.max-retry-sleep:500}")
    int maxTransactionRetrySleep;


    // TODO WHEN TO DELETE THE TASK FROM POSTGRE DATABASE
    @Override
    @RabbitListener(queues = "dispatch.feedback", containerFactory = "feedbackcontainerfactory")
    public void receiveFeedbackFromDispatcher(String taskid) throws InterruptedException {
        int iteration =1;
        while (true){
            try{
                // finish task in transactional method
                _self.processFeedback(taskid);
                break;
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Task " + taskid + " couldn't acquire locks for setting its status to finished. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            } catch (Exception e) {
                logger.error(e.getMessage());
                break;
            }
        }

        // separate while loop in order to prevent _self.processFeedback(taskid); from being called multiple times
        iteration = 1;
        while(true) {
            try {
                // notify scheduler about reschedule
                schedulerService.scheduleTaskWrapper("");
                break;
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Task " + taskid + " couldn't acquire locks for setting reschedule after finishTask. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            } catch (Exception e) {
                logger.error(e.getMessage());
                break;
            }
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void processFeedback(String taskId) {
        logger.info("Task " + taskId + " was processed by the dispatcher");

        Task currentTask = taskService.getTaskById(taskId).orElse(null);
        if(currentTask == null)
            throw new RuntimeException("could not find tasks in postgre database");

        checkAndSetParallelismDegree(currentTask);

        if(currentTask.getModeEnum()== ModeEnum.Sequential)
            checkAndSetSequentialAndIndexNumber(currentTask);

        taskService.finishTask(currentTask);
    }

    //TODO is it necessary to do this also for all grandparent groups??
    @Override
    public void checkAndSetSequentialAndIndexNumber(Task currentTask) {
        Group group = currentTask.getGroup();
        logger.debug("Task " + currentTask.getId() + " update index Number of group " + group.getId());
        group.setLastIndexNumber(group.getLastIndexNumber()+1);
        groupRepository.save(group);
    }

    @Override
    public void checkAndSetParallelismDegree(Task currentTask) {
        // Decrement current parallelismDegree in group of given task
        Optional<Group> updated = groupRepository.decrementCurrentParallelismDegree(currentTask.getGroup().getId());
        if(!updated.isPresent())
            logger.debug("Task " + currentTask.getId() + " failed to decrement currentParallelismDegree due to currentParallelismDegree = 0");
    }
}
