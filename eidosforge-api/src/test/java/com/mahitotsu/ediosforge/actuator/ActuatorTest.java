package com.mahitotsu.ediosforge.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.mahitotsu.ediosforge.AbstractTestBase;

public class ActuatorTest extends AbstractTestBase {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testHealth() {
        this.webTestClient.get().uri("/actuator/health").exchange().expectStatus().isOk();
    }
}
