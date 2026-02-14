package com.example.core.services;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HelloServiceTest {

    @Test
    void testGetMessageWithName() {
        HelloService service = new HelloService();
        String result = service.getMessage("Alice");
        assertEquals("Hello, Alice!", result);
    }

    @Test
    void testGetMessageWithNull() {
        HelloService service = new HelloService();
        String result = service.getMessage(null);
        assertEquals("Hello, World!", result);
    }

    @Test
    void testGetMessageWithEmpty() {
        HelloService service = new HelloService();
        String result = service.getMessage("");
        assertEquals("Hello, World!", result);
    }

    @Test
    void testGetCount() {
        HelloService service = new HelloService();
        assertEquals(42, service.getCount());
    }
}
