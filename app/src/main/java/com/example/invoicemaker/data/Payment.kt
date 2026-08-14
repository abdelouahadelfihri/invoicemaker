package com.example.invoicemaker.data

data class Payment(
    val id: Long,
    val amount: String,       // "500.00 MAD"
    val date: String,         // "12 Jul 2026"
    val method: String,       // "Bank Transfer"
    val reference: String?
)