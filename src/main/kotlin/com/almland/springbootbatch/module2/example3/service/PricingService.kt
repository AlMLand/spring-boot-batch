package com.almland.springbootbatch.module2.example3.service

import com.almland.springbootbatch.module2.example3.service.exception.PricingException
import java.util.Random

/**
 * example to generating an exceptions
 * in a real project service could have been a flaky web service that might fail due to network errors
 */
internal class PricingService(
    val smsPricing: Double,
    val callPricing: Double,
    private val dataPricing: Double
) {

    private val random = Random()

    fun getDataPricing(): Double =
        if (random.nextInt(1000) % 7 == 0) throw PricingException("Error while retrieving data pricing")
        else dataPricing
}
