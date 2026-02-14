package com.example.core.services;

import org.osgi.service.component.annotations.Component;

@Component(service = HelloService.class, property = {
    "service.description=Hello Service"
})
public class HelloService {

    private static final String DEFAULT_MESSAGE = "Hello, World!";

    public String getMessage(String name) {
        if (name == null || name.isEmpty()) {
            return DEFAULT_MESSAGE;
        }
        return "Hello, " + name + "!";
    }

    public int getCount() {
        return 42;
    }
}
