package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.exceptions.LockException;
import com.honeybadgers.models.model.LockResponse;
import com.honeybadgers.realtimescheduler.services.impl.SchedulerService;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class LockRefresherThread extends Thread {

    static final Logger logger = LogManager.getLogger(LockRefresherThread.class);

    LockResponse lockresponse;
    RestTemplate restTemplate;
    final String lockUrl;
    final String name;
    final String value;

    public LockResponse getLockresponse() {
        return lockresponse;
    }

    public LockRefresherThread(LockResponse resp, RestTemplate template, String url) {
        lockresponse = resp;
        restTemplate = template;
        name = lockresponse.getName();
        value = lockresponse.getValue();
        if(url.endsWith("/"))
            lockUrl = url;
        else
            lockUrl = url + "/";
    }

    /**
     * Send a REST request to the lockservice periodically. Tries to refresh current lock status
     */
    @SneakyThrows
    public void run() {
        try {
            HttpEntity<Object> entity = getObjectHttpEntity();
            String url = lockUrl + name + "/" + value;
            while (true) {
                // send Put request
                ResponseEntity<LockResponse> response = restTemplate.exchange(url, HttpMethod.PUT, entity, LockResponse.class);
                if (response.getStatusCode() != HttpStatus.OK) {
                    logger.error("could not refresh lock");
                    throw new LockException("could not refresh lock");
                }

                Thread.sleep(15000);

            }
        } catch (Exception e) {
            if (e.getClass().equals(InterruptedException.class)) {
                releaseLock();
                logger.info(lockresponse.getValue() + " releasing lock cause thread was interrupted by scheduler");
            } else {
                logger.error("error by refreshing lock for lock " + name + "with value " + value + " " + e.getMessage());
                SchedulerService.setStopSchedulerDueToLockAcquisitionException(true);
            }
        }
    }

    /**
     * If an exception is thrown within the lock acquire attempt, this method deletes the current lock.
     * F.e during thread interrupt in scheduler.
     */
    public void releaseLock() {
        HttpEntity<Object> entity = getObjectHttpEntity();
        String url = lockUrl + name + "/" + value;
        ResponseEntity<LockResponse> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, LockResponse.class);
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
