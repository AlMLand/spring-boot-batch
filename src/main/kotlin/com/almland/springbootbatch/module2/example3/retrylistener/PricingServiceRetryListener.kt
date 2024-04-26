package com.almland.springbootbatch.module2.example3.retrylistener

import org.slf4j.LoggerFactory
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener

internal class PricingServiceRetryListener : RetryListener {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun <T : Any?, E : Throwable?> onError(
        context: RetryContext?,
        callback: RetryCallback<T, E>?,
        throwable: Throwable?
    ) {
        logger.error(
            "retryable exception is occurred, count: {}, exception: {}",
            context?.retryCount,
            context?.lastThrowable?.message
        )
        super.onError(context, callback, throwable)
    }
}
