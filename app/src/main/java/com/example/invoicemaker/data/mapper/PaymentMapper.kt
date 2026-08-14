// data/mapper/PaymentMapper.kt
package com.example.invoicemaker.data.mapper

import com.example.invoicemaker.data.local.entity.PaymentEntity
import com.example.invoicemaker.data.Payment
import java.text.SimpleDateFormat
import java.util.*

private val methodLabels = mapOf(
    "CASH" to "Cash",
    "BANK_TRANSFER" to "Bank Transfer",
    "CARD" to "Card",
    "CHECK" to "Check",
    "OTHER" to "Other"
)

fun PaymentEntity.toUiModel(currencySymbol: String): Payment = Payment(
    id = id,
    amount = "%,.2f %s".format(amount, currencySymbol),
    date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(date)),
    method = methodLabels[method] ?: method,
    reference = reference
)