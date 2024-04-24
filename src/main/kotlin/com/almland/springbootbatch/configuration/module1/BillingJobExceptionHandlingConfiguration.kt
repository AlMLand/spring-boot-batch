package com.almland.springbootbatch.configuration.module1

import com.almland.springbootbatch.module1.BillingJobExceptionHandling
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.annotation.Bean

/**
 * class is a placeholder for Spring Batch related beans (Jobs, Steps, etc)
 */
//@Configuration
internal class BillingJobExceptionHandlingConfiguration {

    @Bean
    fun billingJobExceptionHandling(
        jobRepository: JobRepository
    ): BillingJobExceptionHandling = BillingJobExceptionHandling(jobRepository)
}
