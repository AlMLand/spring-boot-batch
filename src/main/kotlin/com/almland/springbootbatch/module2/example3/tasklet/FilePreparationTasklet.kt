package com.almland.springbootbatch.module2.example3.tasklet

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus

internal class FilePreparationTasklet : Tasklet {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private const val JOB_PARAMETER_INPUT_FILE = "input.file"
        private const val JOB_PARAMETER_COPY_TARGET = "input.file.copy.target"
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val jobParameters = contribution.stepExecution.jobParameters
        val inputFileToCopy = jobParameters.getString(JOB_PARAMETER_INPUT_FILE)
        val targetFile = jobParameters.getString(JOB_PARAMETER_COPY_TARGET)

        val source = Paths.get(inputFileToCopy!!)
        val target = Paths.get(targetFile!!)

        logger.info("input file: $inputFileToCopy, is copied to: $targetFile")

        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
        return RepeatStatus.FINISHED
    }
}
