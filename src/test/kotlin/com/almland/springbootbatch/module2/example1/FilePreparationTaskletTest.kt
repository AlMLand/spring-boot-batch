package com.almland.springbootbatch.module2.example1

import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension


@SpringBootTest
@ExtendWith(OutputCaptureExtension::class)
@SpringBatchTest // registers the JobLauncherTestUtils and JobRepositoryTestUtils as Spring beans
internal class FilePreparationTaskletTest(
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
        Files.deleteIfExists(Paths.get("src/main/resources/", "billing/staging/billing-2023-test.csv"))
    }

    @Test
    fun `billing job execution`(output: CapturedOutput) {
        val jobParameters = JobParametersBuilder()
            .addString("input.file", "src/test/resources/billing/billing-2023-test.csv")
            .toJobParameters()

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
        assertTrue(Files.exists(Paths.get("src/main/resources/", "billing/staging/billing-2023-test.csv")))
    }
}