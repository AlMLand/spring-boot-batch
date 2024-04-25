package com.almland.springbootbatch.module2.example3.tasklet

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersValidator

internal class FilePreparationTaskletValidator : JobParametersValidator {

    override fun validate(parameters: JobParameters?) {
        if (
            parameters?.getString("input.file").isNullOrBlank() ||
            parameters?.getString("output.file").isNullOrBlank() ||
            parameters?.getString("input.file.copy.target").isNullOrBlank() ||
            parameters?.getParameter("data.year")?.value.toString().isBlank() ||
            parameters?.getParameter("data.month")?.value.toString().isBlank()
        )
            throw RuntimeException("input file or copy target is null")
    }
}
