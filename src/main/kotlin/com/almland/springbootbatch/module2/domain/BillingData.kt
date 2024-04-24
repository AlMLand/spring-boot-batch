package com.almland.springbootbatch.module2.domain

import com.almland.springbootbatch.module2.annotation.NoArgsConstructor

/**
 * data read from csv file and write to db ( also per row )
 */
@NoArgsConstructor
internal class BillingData(
    var dataYear: Int,
    var dataMonth: Int,
    var accountId: Int,
    var phoneNumber: String,
    var dataUsage: Float,
    var callDuration: Int,
    var smsCount: Int
)
