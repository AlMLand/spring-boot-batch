package com.almland.springbootbatch.configuration.module2.example3

import com.almland.springbootbatch.module2.domain.BillingData
import com.almland.springbootbatch.module2.domain.ReportingData
import com.almland.springbootbatch.module2.example3.BillingDataProcessor
import javax.sql.DataSource
import org.springframework.batch.core.Step
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
        private const val SQL_SELECT_STATEMENT = "select * from BILLING_DATA"
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
        billingDataProcessor: ItemProcessor<BillingData, ReportingData>
    ): Step =
        StepBuilder("reportGeneration", jobRepository)
            .chunk<BillingData, ReportingData>(100, jdbcTransactionManager)
            .reader(billingDataTableReader)
            .processor(billingDataProcessor)
            .writer(billingDataFileWriter)
            .build()

    @Bean
    fun billingDataTableReader(dataSource: DataSource): JdbcCursorItemReader<BillingData> =
        JdbcCursorItemReaderBuilder<BillingData>()
            .name("billingDataDbReader")
            .dataSource(dataSource)
            .sql(SQL_SELECT_STATEMENT)
            .rowMapper(DataClassRowMapper(BillingData::class.java))
            .build()

    @Bean
    fun billingDataFileWriter(): FlatFileItemWriter<ReportingData> =
        FlatFileItemWriterBuilder<ReportingData>()
            .resource(FileSystemResource("src/main/resources/billing/report/billing-2023-report.csv"))
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
        @Value("\${spring.cellular.pricing.sms:0.1}") smsPricing: Double,
        @Value("\${spring.cellular.pricing.call:0.5}") callPricing: Double,
        @Value("\${spring.cellular.pricing.data:0.01}") dataPricing: Double,
        @Value("\${spring.cellular.spending.threshold:150}") spendingThreshold: Double
    ): BillingDataProcessor = BillingDataProcessor(smsPricing, callPricing, dataPricing, spendingThreshold)

}
