package com.almland.springbootbatch.module1

import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.repository.JobRepository

internal class BillingJob(private val jobRepository: JobRepository) : Job {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun getName(): String = "BillingJob"

    override fun execute(execution: JobExecution) {
        logger.info("processing billing information")

        // responsibility of the Job implementation to report its status to the JobRepository (status, exit code,...)
        with(execution) {
            status = BatchStatus.COMPLETED
            exitStatus = ExitStatus.COMPLETED
            jobRepository.update(this)
        }
    }
}
