package com.almland.springbootbatch.configuration.module2.example2

import com.almland.springbootbatch.module2.domain.BillingData
import com.almland.springbootbatch.module2.example1.FilePreparationTasklet
import com.almland.springbootbatch.module2.example1.FilePreparationTaskletValidator
import javax.sql.DataSource
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.core.io.FileSystemResource
import org.springframework.jdbc.support.JdbcTransactionManager

/**
 * class is a placeholder for Spring Batch related beans (Jobs, Steps, etc)
 */
//@Configuration
internal class BillingJobReadFlatFileWriteDbConfiguration {

    companion object {
        private const val SQL_INSERT_STATEMENT =
            "insert into BILLING_DATA values " +
                    "(:dataYear, :dataMonth, :accountId, :phoneNumber, :dataUsage, :callDuration, :smsCount)"
    }

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
    fun billingDataDbWriter(dataSource: DataSource): JdbcBatchItemWriter<BillingData> =
        JdbcBatchItemWriterBuilder<BillingData>()
            .dataSource(dataSource)
            .sql(SQL_INSERT_STATEMENT)
            .beanMapped()
            .build()

    @Bean
    fun stepWriteToDb(
        jobRepository: JobRepository,
        jdbcTransactionManager: JdbcTransactionManager,
        billingDataFileReader: ItemReader<BillingData>,
        billingDataDbWriter: ItemWriter<BillingData>
    ): Step =
        StepBuilder("fileIngestion", jobRepository)
            .chunk<BillingData, BillingData>(100, jdbcTransactionManager)
            .reader(billingDataFileReader)
            .writer(billingDataDbWriter)
            .build()

    @Bean
    fun stepCopyFile(jobRepository: JobRepository, jdbcTransactionManager: JdbcTransactionManager): Step =
        StepBuilder("filePreparation", jobRepository)
            .tasklet(FilePreparationTasklet(), jdbcTransactionManager)
            .build()

    @Bean
    fun job(jobRepository: JobRepository, stepCopyFile: Step, stepWriteToDb: Step): Job =
        JobBuilder("FilePreparationTasklet", jobRepository)
            .validator(FilePreparationTaskletValidator())
            .start(stepCopyFile)
            .next(stepWriteToDb)
            .build()
}
