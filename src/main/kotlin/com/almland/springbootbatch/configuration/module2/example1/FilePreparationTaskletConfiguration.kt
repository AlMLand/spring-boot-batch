package com.almland.springbootbatch.configuration.module2.example1

import com.almland.springbootbatch.module2.example1.FilePreparationTasklet
import com.almland.springbootbatch.module2.example1.FilePreparationTaskletValidator
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.support.JdbcTransactionManager


/**
 * class is a placeholder for Spring Batch related beans (Jobs, Steps, etc.)
 */
//@Configuration
internal class FilePreparationTaskletConfiguration {

    @Bean
    fun step1(jobRepository: JobRepository, jdbcTransactionManager: JdbcTransactionManager): Step =
        StepBuilder("filePreparation", jobRepository)
            .tasklet(FilePreparationTasklet(), jdbcTransactionManager)
            .build()

    @Bean
    fun job(jobRepository: JobRepository, stepCopyFile: Step): Job =
        JobBuilder("FilePreparationTasklet", jobRepository)
            .validator(FilePreparationTaskletValidator())
            .start(stepCopyFile)
            .build()
}
