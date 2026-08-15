package com.example.invoicemaker.data

data class Invoice(
    val id: Long,
    val invoiceNumber: String,   // e.g. "INV-0001"
    val clientName: String,
    val amount: String,          // formatted with currency, e.g. "1,250.00 MAD"
    val issueDate: String,       // e.g. "12 Jul 2026"
    val dueInfo: String,         // e.g. "Due in 7 days" or "Overdue by 3 days"
    val status: InvoiceStatus
)