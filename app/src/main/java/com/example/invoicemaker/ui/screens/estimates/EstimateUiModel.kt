package com.example.invoicemaker.ui.screens.estimates

import com.example.invoicemaker.data.Client
import com.example.invoicemaker.data.Estimate
import com.example.invoicemaker.data.EstimateStatus
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class EstimateUiModel(
    val id: Long,
    val estimateNumber: String,
    val clientName: String,
    val amountFormatted: String,
    val issueDateFormatted: String,
    val validityInfo: String,
    val status: EstimateStatus
)

fun Estimate.toUiModel(client: Client?, currencySymbol: String = "MAD"): EstimateUiModel {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val today = System.currentTimeMillis()
    val diffDays = TimeUnit.MILLISECONDS.toDays(expiryDate - today)

    val status = computedStatus
    val validityInfo = when {
        status == EstimateStatus.ACCEPTED -> "Accepted"
        status == EstimateStatus.REJECTED -> "Rejected"
        status == EstimateStatus.CONVERTED -> "Converted to invoice"
        diffDays < 0 -> "Expired ${-diffDays} day${if (-diffDays == 1L) "" else "s"} ago"
        diffDays == 0L -> "Expires today"
        else -> "Valid for $diffDays more day${if (diffDays == 1L) "" else "s"}"
    }

    // total is a BigDecimal — round to 2 decimals for display.
    val displayTotal = total.setScale(2, RoundingMode.HALF_UP)

    return EstimateUiModel(
        id = id,
        estimateNumber = estimateNumber,
        clientName = client?.name ?: "Unknown Client",
        amountFormatted = "%,.2f %s".format(displayTotal, currencySymbol),
        issueDateFormatted = dateFormat.format(Date(issueDate)),
        validityInfo = validityInfo,
        status = status
    )
}