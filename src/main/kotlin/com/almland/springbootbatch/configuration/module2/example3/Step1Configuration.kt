package com.almland.springbootbatch.configuration.module2.example3

import com.almland.springbootbatch.module2.example3.tasklet.FilePreparationTasklet
import org.springframework.batch.core.Step
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.JdbcTransactionManager

@Configuration
internal class Step1Configuration {

    /**
     * step 1
     */
    @Bean
    fun stepCopyFile(jobRepository: JobRepository, jdbcTransactionManager: JdbcTransactionManager): Step =
        StepBuilder("filePreparation", jobRepository)
            .tasklet(FilePreparationTasklet(), jdbcTransactionManager)
            .build()
}
