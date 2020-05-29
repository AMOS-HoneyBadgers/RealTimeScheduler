package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.realtimescheduler.exception.CreationException;
import com.honeybadgers.realtimescheduler.exception.LimitExceededException;
import com.honeybadgers.realtimescheduler.model.Group;
import com.honeybadgers.realtimescheduler.model.ModeEnum;
import com.honeybadgers.realtimescheduler.model.Task;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.services.IQuartzService;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuartzService implements IQuartzService {

    @Autowired
    TaskPostgresRepository taskRepository;

    @Autowired
    Scheduler scheduler;


    public JobDetail createJobDetails(Task task) {
        return null;
    }


    public Trigger createJobTrigger(Integer priority) {
        return null;
    }


    public void scheduleTask(Task task, Integer totalPriority) {
        assert task != null && totalPriority >= 0;

        JobDetail jobDetail = createJobDetails(task);
        Trigger trigger = createJobTrigger(totalPriority);
    }


    public void scheduleTasks(List<Task> tasks, List<Integer> priorities) {
        assert tasks.size() == priorities.size();

        for (int i = 0; i < tasks.size(); i++) {
            scheduleTask(tasks.get(i), priorities.get(i));
        }
    }


    @Transactional
    public int calculateTaskTotalPriority(Task task) throws LimitExceededException {
        int totalPriority = 0;
        // priority of task is base priority -> most important part
        // deadline of task gets accounted if tasks have same prio -> TODO: would require new prio calc OF ALL TASK ON CHANGE OF ANY TASK
        // retries: higher retries -> lower totalPrio
        if(exceedGroupLimitations(task))
            throw new LimitExceededException("Group limit was exceeded!");

        // TODO enforce sequence number

        // get all tasks with same prio (include yourself) for deadline accounting
        List<Task> allSamePrioTasks = taskRepository.findAllScheduledTasksWithSamePrio(task.getPriority(), task.getId());

        // get DeadlinePrio of all tasks
        Map<Task, Integer> deadlinePrios = applyDeadlineOnPrio(allSamePrioTasks);

        // apply #retries to prio
        Map<Task, Integer> retriesPrios = applyRetriesOnPrio(deadlinePrios);

        // recalculate priorities of found tasks and reschedule them
        Map<Task, Integer> selfExcluded = new HashMap<>(retriesPrios);
        selfExcluded.remove(task);
        scheduleTasks(new ArrayList<>(selfExcluded.keySet()), new ArrayList<>(selfExcluded.values()));

        return retriesPrios.get(task);
    }

    private Map<Task, Integer> applyDeadlineOnPrio(List<Task> tasks) {
        Map<Task, Integer> deadlinePrios = new HashMap<>();
        int offset = 0;
        for(Task task : tasks) {
            if(task.getDeadline() != null)
                offset++;
            deadlinePrios.put(task, task.getPriority() + offset);
        }

        assert deadlinePrios.size() == tasks.size();
        return deadlinePrios;
    }

    private Map<Task, Integer> applyRetriesOnPrio(Map<Task, Integer> deadlinePrios) {
        Map<Task, Integer> mapped = deadlinePrios.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, value -> {
                    // TODO how to apply
                    return value.getValue() + 1;
                }));

        assert deadlinePrios.size() == mapped.size();
        return mapped;
    }

    private boolean exceedGroupLimitations(Task task) {
        boolean paral = false;
        if(task.getModeEnum() == ModeEnum.Parallel) {
            paral = exceedsParallelismDegree(task);
        }

        return paral;
    }

    private boolean exceedsParallelismDegree(Task task) {
        Group group = task.getGroup();
        // get lowestParallelismDegree
        int lowestParallelismDegree = Integer.MAX_VALUE;
        while(group != null) {
            lowestParallelismDegree = (lowestParallelismDegree > group.getParallelismDegree() ? group.getParallelismDegree() : lowestParallelismDegree);
            group = group.getParentGroup();
        }

        // check if lowestParallelismDegree is exceeded -> get all dispatched tasks ()
        // TODO
        return false;
    }
}
