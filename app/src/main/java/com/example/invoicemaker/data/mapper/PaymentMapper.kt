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

fun PaymentEntity.toUiModel(): Payment = Payment(
    id = id,
    invoiceId = invoiceId,
    amount = amount,
    date = date,
    method = method,
    note = note
)