package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.exceptions.LockException;
import com.honeybadgers.models.model.LockResponse;
import org.springframework.stereotype.Service;

@Service
public interface ILockService {

    /**
     * Tries to acquire the lock for the scheduler application. If false, tasks can't be dispatched
     *
     * @return LockResponse Object with values for expiration date
     * @throws LockException when another instance already claims the lock
     */
    LockResponse requestLock() throws LockException;

    /**
     * Wrapper for creating new object of LockRefresherThread class
     * @param lockResponse contains information about the lock (received from ILockService.requestLock())
     * @return new instance of LockRefresherThread
     */
    LockRefresherThread createLockRefreshThread(LockResponse lockResponse);
}
