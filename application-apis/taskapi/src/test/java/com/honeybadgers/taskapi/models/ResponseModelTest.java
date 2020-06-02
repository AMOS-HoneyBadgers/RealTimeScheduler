package com.honeybadgers.taskapi.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseModelTest {

    @Test
    public void testSetterGetterCode() {
        ResponseModel model = new ResponseModel();

        String newCode = "400";
        model.setCode(newCode);
        String get = model.getCode();

        assertEquals(get, newCode);
    }

    @Test
    public void testSetterGetterMessage() {
        ResponseModel model = new ResponseModel();

        String newMessage = "TestMessage";
        model.setMessage(newMessage);
        String get = model.getMessage();

        assertEquals(get, newMessage);
    }

    @Test
    public void testGetObjectWithSetCode() {
        ResponseModel model = new ResponseModel();

        String newCode = "400";
        ResponseModel get = model.code(newCode);

        assertNotNull(get);
        assertEquals(get.getCode(), newCode);
    }

    @Test
    public void testGetObjectWithSetMessage() {
        ResponseModel model = new ResponseModel();

        String newMessage = "NewMessage";
        ResponseModel get = model.message(newMessage);

        assertNotNull(get);
        assertEquals(get.getMessage(), newMessage);
    }

    @Test
    public void testEquals() {
        ResponseModel model1 = new ResponseModel();
        ResponseModel model2 = new ResponseModel();
        ResponseModel model3 = new ResponseModel();
        model3.setCode("400");

        assertTrue(model1.equals(model2));
        assertFalse(model1.equals(model3));
        assertFalse(model2.equals(model3));
    }

    @Test
    public void testHashCode() {
        ResponseModel model1 = new ResponseModel();
        ResponseModel model2 = new ResponseModel();

        assertEquals(model1.hashCode(), model2.hashCode());
    }
}
