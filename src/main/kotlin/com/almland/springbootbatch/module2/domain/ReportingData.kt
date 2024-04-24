package com.almland.springbootbatch.module2.domain

import com.almland.springbootbatch.module2.annotation.NoArgsConstructor

/**
 * data write to the csv
 */
@NoArgsConstructor
internal class ReportingData(
    var billingData: BillingData,
    var billingTotal: Double
)
