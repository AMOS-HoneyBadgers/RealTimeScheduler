package com.honeybadgers.realtimescheduler.services.impl;

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
import org.springframework.beans.factory.annotation.Value;

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

    @Value("${com.realtimescheduler.scheduler.retries-modifier:1}")
    private Double retriesModifier;


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
        Map<Task, Integer> prios;

        // TODO enforce sequence number

        // get all tasks with same prio (include yourself) for deadline accounting
        List<Task> allSamePrioTasks = taskRepository.findAllScheduledTasksWithSamePrio(task.getPriority(), task.getId());

        // apply deadline and type_flag on prio of all tasks
        prios = applyOrderOnPrio(allSamePrioTasks);

        // apply #retries to prio
        prios = applyRetriesOnPrio(prios);

        // recalculate priorities of found tasks and reschedule them
        Map<Task, Integer> selfExcluded = new HashMap<>(prios);
        selfExcluded.remove(task);
        scheduleTasks(new ArrayList<>(selfExcluded.keySet()), new ArrayList<>(selfExcluded.values()));

        return prios.get(task);
    }

    private Map<Task, Integer> applyOrderOnPrio(List<Task> tasks) {
        Map<Task, Integer> deadlinePrios = new HashMap<>();
        int offset = 0;
        for(int i = 0; i < tasks.size(); i++) {
            if(tasks.get(i).getDeadline() != null)
                offset++;
            deadlinePrios.put(tasks.get(i), tasks.get(i).getPriority() + offset);
        }

        assert deadlinePrios.size() == tasks.size();
        return deadlinePrios;
    }

    private Map<Task, Integer> applyRetriesOnPrio(Map<Task, Integer> inputPrios) {
        Map<Task, Integer> outputPrios = inputPrios.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    Task task = entry.getKey();
                    return Math.max(0, entry.getValue() - ((int) Math.round(task.getRetries() * retriesModifier)));
                }));

        assert inputPrios.size() == outputPrios.size();
        return outputPrios;
    }

    /**
     * Check if group limitations have been exceeded (parallelismDegree -> too many tasks already dispatched)
     * To be called, for example, before dispatching a task
     * @param group Group to be checked
     * @throws LimitExceededException
     */
    private void checkGroupLimitations(Group group) throws LimitExceededException {
        assert group != null;

        boolean paral = false;
        if(group.getModeEnum() == ModeEnum.Parallel) {
            paral = exceedsParallelismDegree(group);
        }

        if(paral)
            throw new LimitExceededException("Group limit was exceeded!");
    }

    private boolean exceedsParallelismDegree(Group group) {
        assert group != null;

        Group backup = group;
        // get lowestParallelismDegree
        int lowestParallelismDegree = Integer.MAX_VALUE;
        while(group != null) {
            lowestParallelismDegree = (lowestParallelismDegree > group.getParallelismDegree() ? group.getParallelismDegree() : lowestParallelismDegree);
            group = group.getParentGroup();
        }

        // check if lowestParallelismDegree is exceeded -> get all dispatched tasks ()
        List<Task> dispatched = taskRepository.findAllDispatchedTasks(backup.getId());
        return lowestParallelismDegree <= dispatched.size();
    }
}
