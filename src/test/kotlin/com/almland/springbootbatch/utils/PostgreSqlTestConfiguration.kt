package com.almland.springbootbatch.utils

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
internal class PostgreSqlTestConfiguration {

    @Bean
    @ServiceConnection
    fun postgresDB(): PostgreSQLContainer<Nothing> =
        PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:16.2-alpine")).apply {
            withDatabaseName("alex")
            withUsername("alex")
            withPassword("alex")
        }
}
