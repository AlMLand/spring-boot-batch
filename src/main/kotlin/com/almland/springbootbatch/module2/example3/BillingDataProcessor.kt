package com.almland.springbootbatch.module2.example3

import com.almland.springbootbatch.module2.domain.BillingData
import com.almland.springbootbatch.module2.domain.ReportingData
import com.almland.springbootbatch.module2.example3.service.PricingService
import org.springframework.batch.item.ItemProcessor

internal class BillingDataProcessor(
    private val pricingService: PricingService,
    private var spendingThreshold: Double
) : ItemProcessor<BillingData, ReportingData> {

    /**
     * @return return type is nullable is required -> when not match
     * example for enriching and filtering the data
     */
    override fun process(billingData: BillingData): ReportingData? =
        with(billingData) {
            getBillingTotal()
                .takeIf { it > spendingThreshold }
                ?.let { ReportingData(billingData, it) }
        }

    private fun BillingData.getBillingTotal() =
        dataUsage * pricingService.getDataPricing() +
                callDuration * pricingService.callPricing +
                smsCount * pricingService.smsPricing
}
