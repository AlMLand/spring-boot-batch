package com.almland.springbootbatch.module2.example3

import com.almland.springbootbatch.module2.domain.BillingData
import com.almland.springbootbatch.module2.domain.ReportingData
import org.springframework.batch.item.ItemProcessor

internal class BillingDataProcessor(
    private var smsPricing: Double,
    private var callPricing: Double,
    private var dataPricing: Double,
    private var spendingThreshold: Double
) : ItemProcessor<BillingData, ReportingData> {

    /**
     * @return return type is nullable is required -> when not match
     * example for enriching and filtering the data
     */
    override fun process(billingData: BillingData): ReportingData? =
        with(billingData) {
            (dataUsage * dataPricing + callDuration * callPricing + smsCount * smsPricing)
                .takeIf { it > spendingThreshold }
                ?.let { ReportingData(billingData, it) }
        }
}
