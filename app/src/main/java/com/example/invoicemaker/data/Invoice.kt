package com.yourpackage.metalconstructions.data

import com.example.invoicemaker.data.InvoiceLine
import java.math.BigDecimal

enum class InvoiceStatus(val label: String) {
    UNPAID("Unpaid"),
    PARTIALLY_PAID("Partially Paid"),
    OVERDUE("Overdue"),
    PAID("Paid"),
    CANCELLED("Cancelled")
}

data class Payment(
    val id: Long = 0,
    val invoiceId: Long,
    val amount: BigDecimal,
    val date: Long,
    val method: String? = null,             // e.g. "Cash", "Bank Transfer", "Cheque"
    val note: String? = null
)

data class Invoice(
    val id: Long = 0,
    val invoiceNumber: String,              // e.g. "INV-0001"
    val clientId: Long,
    val lineItems: List<InvoiceLine> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val status: InvoiceStatus = InvoiceStatus.UNPAID,
    val issueDate: Long,
    val dueDate: Long,
    val notes: String? = null,
    val sourceEstimateId: Long? = null,     // set if this invoice came from an estimate
    val createdAt: Long = System.currentTimeMillis()
) {
    val subtotal: BigDecimal get() = lineItems.sumOf { it.subtotal }
    val totalTax: BigDecimal get() = lineItems.sumOf { it.taxAmount }
    val total: BigDecimal get() = lineItems.sumOf { it.total }
    val amountPaid: BigDecimal get() = payments.sumOf { it.amount }
    val amountDue: BigDecimal get() = total.subtract(amountPaid)

    val computedStatus: InvoiceStatus
        get() = when {
            status == InvoiceStatus.CANCELLED -> InvoiceStatus.CANCELLED
            amountDue <= BigDecimal.ZERO -> InvoiceStatus.PAID
            amountPaid > BigDecimal.ZERO -> InvoiceStatus.PARTIALLY_PAID
            System.currentTimeMillis() > dueDate -> InvoiceStatus.OVERDUE
            else -> InvoiceStatus.UNPAID
        }
}