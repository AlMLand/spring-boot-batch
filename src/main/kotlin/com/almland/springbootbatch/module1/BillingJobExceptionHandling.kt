package com.almland.springbootbatch.module1

import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.repository.JobRepository

internal class BillingJobExceptionHandling(private val jobRepository: JobRepository) : Job {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun getName(): String = "BillingJobExceptionHandling"

    override fun execute(execution: JobExecution) {
        with(execution) {
            try {
                throw RuntimeException("Unable to process billing information")
            } catch (e: Exception) {
                addFailureException(e)
                status = BatchStatus.COMPLETED
                exitStatus = ExitStatus.FAILED.addExitDescription(e.message ?: "no exception message")
            } finally {
                jobRepository.update(this)
            }
        }
    }
}