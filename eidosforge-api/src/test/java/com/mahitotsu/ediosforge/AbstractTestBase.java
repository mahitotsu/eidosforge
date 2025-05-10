package com.mahitotsu.ediosforge;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
public abstract class AbstractTestBase {
}
