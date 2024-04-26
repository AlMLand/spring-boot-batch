package com.almland.springbootbatch.configuration.module2.example3

import com.almland.springbootbatch.module2.domain.BillingData
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import org.springframework.batch.core.SkipListener
import org.springframework.batch.item.file.FlatFileParseException

internal open class BillingDataSkipListener(skippedItemFile: String) : SkipListener<BillingData, BillingData> {

    private val skippedItemFilePath: Path by lazy { Paths.get(skippedItemFile) }

    override fun onSkipInRead(t: Throwable) {
        (t as? FlatFileParseException)
            .also { flatFileParseException ->
                val rawLine = flatFileParseException?.input
                val lineNumber = flatFileParseException?.lineNumber
                val skippenLine = "$lineNumber|$rawLine${System.lineSeparator()}"

                try {
                    Files.writeString(
                        skippedItemFilePath,
                        skippenLine,
                        StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE
                    )
                } catch (e: IOException) {
                    throw RuntimeException("unable to write skipped item: $skippenLine")
                }
            }
    }
}