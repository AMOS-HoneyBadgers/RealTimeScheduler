package com.honeybadgers.taskapi.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorModelTest {

    @Test
    public void testSetterGetterCode() {
        ErrorModel model = new ErrorModel();

        String newCode = "400";
        model.setCode(newCode);
        String get = model.getCode();

        assertEquals(get, newCode);
    }

    @Test
    public void testSetterGetterMessage() {
        ErrorModel model = new ErrorModel();

        String newMessage = "TestMessage";
        model.setErrorMessage(newMessage);
        String get = model.getErrorMessage();

        assertEquals(get, newMessage);
    }

    @Test
    public void testGetObjectWithSetCode() {
        ErrorModel model = new ErrorModel();

        String newCode = "400";
        ErrorModel get = model.code(newCode);

        assertNotNull(get);
        assertEquals(get.getCode(), newCode);
    }

    @Test
    public void testGetObjectWithSetMessage() {
        ErrorModel model = new ErrorModel();

        String newMessage = "NewMessage";
        ErrorModel get = model.errorMessage(newMessage);

        assertNotNull(get);
        assertEquals(get.getErrorMessage(), newMessage);
    }

    @Test
    public void testEquals() {
        ErrorModel model1 = new ErrorModel();
        ErrorModel model2 = new ErrorModel();
        ErrorModel model3 = new ErrorModel();
        model3.setCode("400");

        assertTrue(model1.equals(model2));
        assertFalse(model1.equals(model3));
        assertFalse(model2.equals(model3));
    }

    @Test
    public void testHashCode() {
        ErrorModel model1 = new ErrorModel();
        ErrorModel model2 = new ErrorModel();

        assertEquals(model1.hashCode(), model2.hashCode());
    }
}
