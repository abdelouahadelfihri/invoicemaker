package com.example.invoicemaker.data

data class Estimate(
    val id: Long,
    val estimateNumber: String,
    val clientName: String,
    val amount: String,
    val issueDate: String,
    val expiryInfo: String,      // "Expires in 5 days" / "Expired"
    val status: EstimateStatus
)