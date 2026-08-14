// data/EstimateStatus.kt
package com.example.invoicemaker.data

enum class EstimateStatus(val label: String) {
    DRAFT("Draft"),
    SENT("Sent"),
    ACCEPTED("Accepted"),
    DECLINED("Declined"),
    EXPIRED("Expired"),
    CONVERTED("Converted")
}

// data/Estimate.kt
data class Estimate(
    val id: Long,
    val estimateNumber: String,
    val clientName: String,
    val amount: String,
    val issueDate: String,
    val expiryInfo: String,      // "Expires in 5 days" / "Expired"
    val status: EstimateStatus
)