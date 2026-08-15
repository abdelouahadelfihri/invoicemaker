package com.example.invoicemaker.ui.invoices

import com.example.invoicemaker.data.Client
import com.example.invoicemaker.data.Invoice
import com.example.invoicemaker.data.InvoiceStatus
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class InvoiceUiModel(
    val id: Long,
    val invoiceNumber: String,
    val clientName: String,
    val amountFormatted: String,
    val issueDateFormatted: String,
    val dueInfo: String,
    val status: InvoiceStatus
)

fun Invoice.toUiModel(client: Client?, currencySymbol: String = "MAD"): InvoiceUiModel {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val today = System.currentTimeMillis()
    val diffDays = TimeUnit.MILLISECONDS.toDays(dueDate - today)

    val status = computedStatus
    val dueInfo = when {
        status == InvoiceStatus.PAID -> "Paid"
        status == InvoiceStatus.CANCELLED -> "Cancelled"
        diffDays < 0 -> "Overdue by ${-diffDays} day${if (-diffDays == 1L) "" else "s"}"
        diffDays == 0L -> "Due today"
        else -> "Due in $diffDays day${if (diffDays == 1L) "" else "s"}"
    }

    return InvoiceUiModel(
        id = id,
        invoiceNumber = invoiceNumber,
        clientName = client?.name ?: "Unknown Client",
        amountFormatted = "%,.2f %s".format(total, currencySymbol),
        issueDateFormatted = dateFormat.format(Date(issueDate)),
        dueInfo = dueInfo,
        status = status
    )
}