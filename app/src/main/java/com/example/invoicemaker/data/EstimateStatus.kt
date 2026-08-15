package com.example.invoicemaker.data

enum class EstimateStatus(val label: String) {
    DRAFT("Draft"),
    SENT("Sent"),
    ACCEPTED("Accepted"),
    DECLINED("Declined"),
    EXPIRED("Expired"),
    CONVERTED("Converted")
}