package com.almland.springbootbatch.configuration.module2.example3

import com.almland.springbootbatch.module2.domain.BillingData
import com.almland.springbootbatch.module2.domain.ReportingData
import com.almland.springbootbatch.module2.example3.BillingDataProcessor
import com.almland.springbootbatch.module2.example3.retrylistener.PricingServiceRetryListener
import com.almland.springbootbatch.module2.example3.service.PricingService
import com.almland.springbootbatch.module2.example3.service.exception.PricingException
import javax.sql.DataSource
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.support.JdbcTransactionManager

@Configuration
internal class Step3Configuration {

    companion object {
        private const val SQL_SELECT_STATEMENT = "select * from BILLING_DATA where DATA_YEAR = %d and DATA_MONTH = %d"
    }

    /**
     * step 3
     */
    @Bean
    fun stepWriteToFile(
        jobRepository: JobRepository,
        jdbcTransactionManager: JdbcTransactionManager,
        billingDataTableReader: ItemReader<BillingData>,
        billingDataFileWriter: ItemWriter<ReportingData>,
        billingDataProcessor: ItemProcessor<BillingData, ReportingData>,
        pricingServiceRetryListener: PricingServiceRetryListener
    ): Step =
        StepBuilder("reportGeneration", jobRepository)
            .chunk<BillingData, ReportingData>(100, jdbcTransactionManager)
            .reader(billingDataTableReader)
            .processor(billingDataProcessor)
            .writer(billingDataFileWriter)
            .faultTolerant()
            .retry(PricingException::class.java)
            .retryLimit(100)
            .listener(pricingServiceRetryListener)
            .build()

    @Bean
    fun pricingServiceRetryListener(): PricingServiceRetryListener = PricingServiceRetryListener()

    @Bean
    @StepScope
    fun billingDataTableReader(
        dataSource: DataSource,
        @Value("#{jobParameters['data.year']}") year: Int,
        @Value("#{jobParameters['data.month']}") month: Int
    ): JdbcCursorItemReader<BillingData> =
        JdbcCursorItemReaderBuilder<BillingData>()
            .name("billingDataDbReader")
            .dataSource(dataSource)
            .sql(String.format(SQL_SELECT_STATEMENT, year, month))
            .rowMapper(DataClassRowMapper(BillingData::class.java))
            .build()

    @Bean
    @StepScope
    fun billingDataFileWriter(@Value("#{jobParameters['output.file']}") outputFile: String): FlatFileItemWriter<ReportingData> =
        FlatFileItemWriterBuilder<ReportingData>()
            .resource(FileSystemResource(outputFile))
            .name("billingDataFileWriter")
            .delimited()
            .names(
                "billingData.dataYear",
                "billingData.dataMonth",
                "billingData.accountId",
                "billingData.phoneNumber",
                "billingData.dataUsage",
                "billingData.callDuration",
                "billingData.smsCount",
                "billingTotal"
            )
            .build()

    @Bean
    fun billingDataProcessor(
        pricingService: PricingService,
        @Value("\${spring.cellular.spending.threshold:150}") spendingThreshold: Double
    ): BillingDataProcessor = BillingDataProcessor(pricingService, spendingThreshold)

    @Bean
    fun pricingService(
        @Value("\${spring.cellular.pricing.sms:0.1}") smsPricing: Double,
        @Value("\${spring.cellular.pricing.call:0.5}") callPricing: Double,
        @Value("\${spring.cellular.pricing.data:0.01}") dataPricing: Double
    ): PricingService = PricingService(smsPricing, callPricing, dataPricing)

}
