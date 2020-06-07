package com.honeybadgers.realtimescheduler;


import com.honeybadgers.models.RedisLock;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication(scanBasePackages = "com.honeybadgers")
@Slf4j
public class RealtimeschedulerApplication {

    @Value("${dispatcher.capacity}")
    String dispatcherCapacity;

    @Value("${dispatcher.capacity.id}")
    String dispatcherCapacityId;


    @Autowired
    LockRedisRepository redisRepository;

    public static void main(String[] args) {
        SpringApplication.run(RealtimeschedulerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCapacityAfterStartup() {
        // Initialize Capacity only when there is no capazity present
        RedisLock capacity = redisRepository.findById("Capacity").orElse(null);
        if(capacity == null){
            capacity = new RedisLock();
            capacity.setId(dispatcherCapacityId);
            capacity.setCapacity(Integer.parseInt(dispatcherCapacity));
        }
    }

}
