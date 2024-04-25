package com.almland.springbootbatch.module2.example3

import com.almland.springbootbatch.utils.PostgreSqlTestConfiguration
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
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.jdbc.JdbcTestUtils

@SpringBootTest
@Import(PostgreSqlTestConfiguration::class)
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
        Files.deleteIfExists(Paths.get("src/test/resources/", "billing/report/billing-2023-report.csv"))
    }

    @Test
    fun `billing job execution`() {
        val jobParameters = JobParametersBuilder()
            .addString("input.file.copy.target", "src/test/resources/billing/copy/billing-2023-01-test.csv")
            .addString("input.file", "src/test/resources/billing/billing-2023-01-test.csv")
            .addString("output.file", "src/test/resources/billing/report/billing-2023-01-test-report.csv")
            .addJobParameter("data.year", 2023, Int::class.java)
            .addJobParameter("data.month", 1, Int::class.java)
            .toJobParameters()

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
        assertTrue(Files.exists(Paths.get("src/test/resources/", "billing/copy/billing-2023-01-test.csv")))
        assertEquals(1000, JdbcTestUtils.countRowsInTable(jdbcTemplate, DB_TABLE_NAME))

        with(Paths.get("src/test/resources/", "billing/report/billing-2023-01-test-report.csv")) {
            assertTrue(Files.exists(this))
            assertEquals(781, Files.lines(this).count())
        }
    }
}
