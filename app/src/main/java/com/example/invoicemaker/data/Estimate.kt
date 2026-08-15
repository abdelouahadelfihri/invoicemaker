package com.example.invoicemaker.data

import java.math.BigDecimal

enum class EstimateStatus(val label: String) {
    DRAFT("Draft"),
    SENT("Sent"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    EXPIRED("Expired"),
    CONVERTED("Converted to Invoice")
}

data class Estimate(
    val id: Long = 0,
    val estimateNumber: String,             // e.g. "EST-0001"
    val clientId: Long,
    val lineItems: List<LineItem> = emptyList(),
    val status: EstimateStatus = EstimateStatus.DRAFT,
    val issueDate: Long,
    val expiryDate: Long,
    val notes: String? = null,
    val termsAndConditions: String? = null,
    val convertedInvoiceId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val subtotal: BigDecimal get() = lineItems.sumOf { it.subtotal }
    val totalTax: BigDecimal get() = lineItems.sumOf { it.taxAmount }
    val total: BigDecimal get() = lineItems.sumOf { it.total }
}