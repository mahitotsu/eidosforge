package com.mahitotsu.ediosforge;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestRuntimeEnvironment {

    @Autowired
    private Environment env;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TestRestTemplate testRestTemplate() {

        final String port = this.env.getProperty("local.server.port");
        return new TestRestTemplate(new RestTemplateBuilder().rootUri("http://localhost:" + port));
    }

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {

        final DockerImageName imageName = DockerImageName
                .parse("public.ecr.aws/docker/library/postgres:15.4-alpine3.18").asCompatibleSubstituteFor("postgres");

        final PostgreSQLContainer<?> psql = new PostgreSQLContainer<>(imageName);
        psql.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(PostgreSQLContainer.class)));

        return psql;
    }
}
