package com.almland.springbootbatch.module2.example1

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersValidator

internal class FilePreparationTaskletValidator : JobParametersValidator {

    override fun validate(parameters: JobParameters?) {
        if (parameters?.getString("input.file.to.copy") == null)
            throw RuntimeException("input file is null")
    }
}
