package com.almland.springbootbatch.module2.example3

import com.almland.springbootbatch.utils.PostgreSqlTestConfiguration
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
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
    @Autowired private val jobLauncherTestUtils: JobLauncherTestUtils
) {

    companion object {
        private const val DB_TABLE_NAME = "BILLING_DATA"

        @JvmStatic
        fun getBillingJobTestData(): Stream<Arguments> = Stream.of(
            Arguments.arguments(
                "src/test/resources/billing/copy/billing-2023-01-test.csv",
                "src/test/resources/billing/billing-2023-01-test.csv",
                "src/test/resources/billing/report/billing-2023-01-test-report.csv",
                2023,
                1,
                781
            ),
            Arguments.arguments(
                "src/test/resources/billing/copy/billing-2023-02-test.csv",
                "src/test/resources/billing/billing-2023-02-test.csv",
                "src/test/resources/billing/report/billing-2023-02-test-report.csv",
                2023,
                2,
                781
            )
        )
    }

    @BeforeEach
    fun setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, DB_TABLE_NAME)
    }

    @ParameterizedTest
    @MethodSource("getBillingJobTestData")
    fun `billing job execution file`(
        copyFileTarget: String,
        inputFile: String,
        reportFileTarget: String,
        year: Int,
        month: Int,
        expectedDataCount: Long
    ) {
        val jobParameters = JobParametersBuilder()
            .addString("input.file.copy.target", copyFileTarget)
            .addString("input.file", inputFile)
            .addString("output.file", reportFileTarget)
            .addJobParameter("data.year", year, Int::class.java)
            .addJobParameter("data.month", month, Int::class.java)
            .toJobParameters()

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
        assertTrue(
            Files.exists(
                Paths.get(
                    copyFileTarget.substringBefore("billing"),
                    copyFileTarget.substringAfter("resources/")
                )
            )
        )
        assertEquals(1000, JdbcTestUtils.countRowsInTable(jdbcTemplate, DB_TABLE_NAME))

        with(Paths.get(reportFileTarget.substringBefore("billing"), reportFileTarget.substringAfter("resources/"))) {
            assertTrue(Files.exists(this))
            assertEquals(expectedDataCount, Files.lines(this).count())
        }
    }
}
