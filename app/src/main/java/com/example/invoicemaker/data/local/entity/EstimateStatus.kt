package com.example.invoicemaker.data.local.entity

enum class EstimateStatus(val label: String) {
    DRAFT("Draft"),
    SENT("Sent"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    EXPIRED("Expired")
}