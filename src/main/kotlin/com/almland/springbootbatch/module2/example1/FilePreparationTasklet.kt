package com.almland.springbootbatch.module2.example1

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus

// from root -> java -jar build/libs/spring-boot-batch-0.0.1-SNAPSHOT.jar input.file=src/main/resources/billing/billing-2023-01.csv
internal class FilePreparationTasklet : Tasklet {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private const val JOB_PARAMETER_INPUT_FILE = "input.file"
        private const val PATH_TO_STAGE_FOLGER = "src/main/resources/billing/staging/"
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val jobParameters = contribution.stepExecution.jobParameters
        val inputFile = jobParameters.getString(JOB_PARAMETER_INPUT_FILE)
        val source = Paths.get(inputFile!!)
        logger.info("input file: $inputFile")
        val target = Paths.get(PATH_TO_STAGE_FOLGER, source.toFile().name)
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
        return RepeatStatus.FINISHED
    }
}
