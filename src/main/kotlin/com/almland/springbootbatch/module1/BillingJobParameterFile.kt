package com.almland.springbootbatch.module1

import org.slf4j.LoggerFactory
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.repository.JobRepository

/**
 * The input file will be used as an identifying Job parameter named "input.file"
 */
internal class BillingJobParameterFile(private val jobRepository: JobRepository) : Job {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun getName(): String = "BillingJobParameterFile"

    override fun execute(execution: JobExecution) {
        logger.info("processing billing information from file: ${execution.jobParameters.getString("input.file")}")

        with(execution) {
            status = BatchStatus.COMPLETED
            exitStatus = ExitStatus.COMPLETED
            jobRepository.update(this)
        }
    }
}
