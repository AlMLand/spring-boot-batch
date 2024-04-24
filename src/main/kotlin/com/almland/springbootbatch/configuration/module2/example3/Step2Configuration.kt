package com.almland.springbootbatch.configuration.module2.example3

import com.almland.springbootbatch.module2.domain.BillingData
import javax.sql.DataSource
import org.springframework.batch.core.Step
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.jdbc.support.JdbcTransactionManager

@Configuration
internal class Step2Configuration {

    companion object {
        private const val SQL_INSERT_STATEMENT =
            "insert into BILLING_DATA values " +
                    "(:dataYear, :dataMonth, :accountId, :phoneNumber, :dataUsage, :callDuration, :smsCount)"
    }

    /**
     * step 2
     */
    @Bean
    fun stepWriteToTable(
        jobRepository: JobRepository,
        jdbcTransactionManager: JdbcTransactionManager,
        billingDataFileReader: ItemReader<BillingData>,
        billingDataTableWriter: ItemWriter<BillingData>
    ): Step =
        StepBuilder("fileIngestion", jobRepository)
            .chunk<BillingData, BillingData>(100, jdbcTransactionManager)
            .reader(billingDataFileReader)
            .writer(billingDataTableWriter)
            .build()

    @Bean
    fun billingDataFileReader(): FlatFileItemReader<BillingData> =
        FlatFileItemReaderBuilder<BillingData>()
            .name("billingDataFileReader")
            .resource(FileSystemResource("src/main/resources/billing/staging/billing-2023-test.csv"))
            .delimited()
            .names("dataYear", "dataMonth", "accountId", "phoneNumber", "dataUsage", "callDuration", "smsCount")
            .targetType(BillingData::class.java)
            .build()

    @Bean
    fun billingDataTableWriter(dataSource: DataSource): JdbcBatchItemWriter<BillingData> =
        JdbcBatchItemWriterBuilder<BillingData>()
            .dataSource(dataSource)
            .sql(SQL_INSERT_STATEMENT)
            .beanMapped()
            .build()
}
