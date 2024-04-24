package com.almland.springbootbatch.module2.example3

import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.jdbc.JdbcTestUtils

@SpringBootTest
@SpringBatchTest // registers the JobLauncherTestUtils and JobRepositoryTestUtils as Spring beans
internal class BillingJobReadFlatFileWriteDbReadDbWriteFlatFileConfigurationTest(
    @Autowired private val jdbcTemplate: JdbcTemplate,
    @Autowired private val jobLauncherTestUtils: JobLauncherTestUtils,
    @Autowired private val jobRepositoryTestUtils: JobRepositoryTestUtils
) {

    companion object {
        private const val DB_TABLE_NAME = "BILLING_DATA"
    }

    @BeforeEach
    fun setUp() {
        jobRepositoryTestUtils.removeJobExecutions()
        JdbcTestUtils.deleteFromTables(jdbcTemplate, DB_TABLE_NAME)
    }

    @AfterEach
    fun tearDown() {
        jobRepositoryTestUtils.removeJobExecutions()
        Files.deleteIfExists(Paths.get("src/main/resources/", "billing/staging/billing-2023-test.csv"))
        Files.deleteIfExists(Paths.get("src/main/resources/", "billing/report/billing-2023-report.csv"))
    }

    @Test
    fun `billing job execution`() {
        val jobParameters = JobParametersBuilder()
            .addString("input.file", "src/test/resources/billing/billing-2023-test.csv")
            .toJobParameters()

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
        assertTrue(Files.exists(Paths.get("src/main/resources/", "billing/staging/billing-2023-test.csv")))
        assertEquals(1000, JdbcTestUtils.countRowsInTable(jdbcTemplate, DB_TABLE_NAME))

        with(Paths.get("src/main/resources/", "billing/report/billing-2023-report.csv")) {
            assertTrue(Files.exists(this))
            assertEquals(781, Files.lines(this).count())
        }
    }
}
