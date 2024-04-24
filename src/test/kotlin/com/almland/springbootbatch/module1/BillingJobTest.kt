package com.almland.springbootbatch.module1

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParameters
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
internal class BillingJobTest(
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
    fun `billing job execution`(output: CapturedOutput) {
        val jobParameters = JobParameters()

        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        assertTrue(output.out.contains("processing billing information"))
        assertEquals(ExitStatus.COMPLETED, jobExecution.exitStatus)
    }
}