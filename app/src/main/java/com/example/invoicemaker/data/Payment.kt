package com.example.invoicemaker.data

import java.math.BigDecimal

data class Payment(
    val id: Long = 0,
    val invoiceId: Long = 0,
    val amount: BigDecimal,
    val date: Long,
    val method: String? = null,
    val note: String? = null
)