package com.honeybadgers.realtimescheduler.config;

import com.honeybadgers.models.RedisLock;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventListenerStartup {

    @Value("${dispatcher.capacity}")
    String dispatcherCapacity;

    @Value("${dispatcher.capacity.id}")
    String dispatcherCapacityId;

    @Autowired
    LockRedisRepository redisRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCapacityAfterStartup() {
        // Initialize Capacity only when there is no capazity present
        RedisLock capacity = redisRepository.findById(dispatcherCapacityId).orElse(null);
        if(capacity == null){
            System.out.println("####First Step for REDIS DB: initialize capacity value####");
            capacity = new RedisLock();
            capacity.setId(dispatcherCapacityId);
            capacity.setCapacity(Integer.parseInt(dispatcherCapacity));
            redisRepository.save(capacity);
        }
        else
            System.out.println("disspatchercapacity was initalized already");
    }
}
