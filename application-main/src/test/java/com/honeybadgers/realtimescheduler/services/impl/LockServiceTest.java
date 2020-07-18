package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.exceptions.LockException;
import com.honeybadgers.models.model.LockResponse;
import com.honeybadgers.realtimescheduler.services.ILockService;
import com.honeybadgers.realtimescheduler.services.LockRefresherThread;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LockService.class)
public class LockServiceTest {

    @MockBean
    RestTemplate restTemplate;

    @Autowired
    LockService service;

    @Test
    public void testRequestLock() {
        //Arrange
        LockResponse lockResponse = new LockResponse();
        lockResponse.setName("SCHEDULER");
        lockResponse.setValue("value");
        when(restTemplate.postForEntity(anyString(), any(), any(), any(Class.class))).thenReturn(ResponseEntity.ok(lockResponse));
        //Act
        ILockService spy = spy(service);
        LockResponse response = spy.requestLock();

        assertNotNull(response);
        assertEquals(lockResponse, response);
    }

    @Test(expected = LockException.class)
    public void testRequestLock_WithBAD_REQUESTThrowsLockException() {
        //Arrange
        when(restTemplate.postForEntity(anyString(), any(), any(), any(Class.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
        //Act
        ILockService spy = spy(service);
        spy.requestLock();

    }

    @Test
    public void testCreateLockRefreshThread() {
        LockResponse lockResponse = new LockResponse();
        lockResponse.setName("SCHEDULER");
        lockResponse.setValue("value");

        LockRefresherThread thread = service.createLockRefreshThread(lockResponse);

        assertNotNull(thread);
        assertEquals(lockResponse, thread.getLockresponse());
    }
}
