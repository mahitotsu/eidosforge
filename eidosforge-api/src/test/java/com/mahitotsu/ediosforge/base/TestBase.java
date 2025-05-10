package com.mahitotsu.ediosforge.base;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import com.mahitotsu.ediosforge.base.TestBase.Containers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import({ Containers.class })
@Execution(ExecutionMode.CONCURRENT)
@TestInstance(Lifecycle.PER_METHOD)
public abstract class TestBase {

    @TestConfiguration
    public static class Containers {

        @Bean
        @ServiceConnection
        public PostgreSQLContainer<?> postgreSQLContainer() {

            final DockerImageName imageName = DockerImageName
                    .parse("public.ecr.aws/docker/library/postgres:15.4-alpine3.18")
                    .asCompatibleSubstituteFor("postgres");

            final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(imageName);
            postgres.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(imageName.asCanonicalNameString())));

            return postgres;
        }
    }

}
