package com.almland.springbootbatch.module1

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.core.io.ResourceLoader

@SpringBootTest
@ExtendWith(OutputCaptureExtension::class)
@SpringBatchTest // registers the JobLauncherTestUtils and JobRepositoryTestUtils as Spring beans
internal class BillingJobParameterFileTest(
    @Autowired private val resourceLoader: ResourceLoader,
    @Autowired private val jobLauncherTestUtils: JobLauncherTestUtils,
    @Autowired private val jobRepositoryTestUtils: JobRepositoryTestUtils
) {

    @BeforeEach
    fun setUp() {
        jobRepositoryTestUtils.removeJobExecutions()
    }

    @AfterEach
    fun tearDown() {
        jobRepositoryTestUtils.removeJobExecutions()
    }

    @Test
    fun `billing job parameter file execution 1`(output: CapturedOutput) {
        val jobParameters = jobLauncherTestUtils
            .uniqueJobParametersBuilder
            .addString(
                "input.file",
                resourceLoader
                    .getResource("classpath:billing/billing-2023-test.csv")
                    .filename
                    ?: "file has no name"
            )
            .addString("file.format", "csv")
            .toJobParameters()

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        assertTrue(output.out.contains("processing billing information from file: billing-2023-test.csv"))
        assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
    }

    @Test
    fun `billing job parameter file execution 2`(output: CapturedOutput) {
        val jobParameters = jobLauncherTestUtils
            .uniqueJobParametersBuilder
            .addString("input.file", "/some/input/file")
            .toJobParameters()

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        assertTrue(output.out.contains("processing billing information from file: /some/input/file"))
        assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
    }
}
