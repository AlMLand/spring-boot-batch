package com.almland.springbootbatch.configuration.module2.example3

import com.almland.springbootbatch.module2.example1.FilePreparationTaskletValidator
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * class is a placeholder for Spring Batch related beans (Jobs, Steps, etc)
 */
@Configuration
internal class BillingJobReadFlatFileWriteDbReadDbWriteFlatFileConfiguration {

    @Bean
    fun job(jobRepository: JobRepository, stepCopyFile: Step, stepWriteToTable: Step, stepWriteToFile: Step): Job =
        JobBuilder("FilePreparationTasklet", jobRepository)
            .validator(FilePreparationTaskletValidator())
            .start(stepCopyFile)
            .next(stepWriteToTable)
            .next(stepWriteToFile)
            .build()
}
