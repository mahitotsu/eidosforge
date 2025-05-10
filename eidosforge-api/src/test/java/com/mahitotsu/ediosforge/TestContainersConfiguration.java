package com.mahitotsu.ediosforge;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfiguration {
    
    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {

        final PostgreSQLContainer<?> psql = new PostgreSQLContainer<>(DockerImageName.parse(
                "public.ecr.aws/docker/library/postgres:15.4-alpine3.18").asCompatibleSubstituteFor("postgres"));
        return psql;
    }
}
