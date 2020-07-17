package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.exceptions.LockException;
import com.honeybadgers.models.model.LockResponse;
import com.honeybadgers.realtimescheduler.services.ILockService;
import com.honeybadgers.realtimescheduler.services.LockRefresherThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class LockService implements ILockService {

    static final Logger logger = LogManager.getLogger(LockService.class);

    @Value("${com.honeybadgers.lockservice.url}")
    String lockServiceUrl;

    @Autowired
    RestTemplate restTemplate;


    @Override
    public LockResponse requestLock() throws LockException {
        final String scheduler = "SCHEDULER";

        // create headers
        HttpEntity<Object> entity = getObjectHttpEntity();
        try {
            // send POST request
            ResponseEntity<LockResponse> response = restTemplate.postForEntity(lockServiceUrl + scheduler, entity, LockResponse.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                logger.info("lock for scheduler already acquired");
                throw new LockException("Failed to acquire lock for Lock Application");
            }
            logger.info("acquired lock for: " + response.getBody().getValue());
            return response.getBody();
        } catch (Exception e) {
            throw new LockException("error by acquiring scheduler lock " + e.getMessage());
        }
    }

    @Override
    public LockRefresherThread createLockRefreshThread(LockResponse lockResponse) {
        return new LockRefresherThread(lockResponse, restTemplate, lockServiceUrl);
    }

    /**
     * Wrapper method for http entity creation
     *
     * @return HttpEntity instance
     */
    private static HttpEntity<Object> getObjectHttpEntity() {
        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // build the request
        return new HttpEntity<>(null, headers);
    }
}
