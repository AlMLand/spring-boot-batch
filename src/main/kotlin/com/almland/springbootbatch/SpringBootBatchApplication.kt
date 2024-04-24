package com.almland.springbootbatch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// List of ItemReaders and ItemWriters: https://docs.spring.io/spring-batch/docs/5.0.4/reference/html/appendix.html#itemReadersAppendix
// https://spring.academy/courses/building-a-batch-application-with-spring-batch
@SpringBootApplication
class SpringBootBatchApplication

fun main(args: Array<String>) {
    runApplication<SpringBootBatchApplication>(*args)
}
