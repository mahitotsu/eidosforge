package com.mahitotsu.ediosforge.actuator;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import com.mahitotsu.ediosforge.TestBase;

public class ActuatorTest extends TestBase {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testHealth() {
        assertEquals(HttpStatus.OK,
                this.testRestTemplate.getForEntity("/actuator/healt", String.class).getStatusCode());
    }
}
