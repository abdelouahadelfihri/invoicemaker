package com.example.invoicemaker.data

enum class InvoiceStatus(val label: String) {
    DRAFT("Draft"),
    UNPAID("Unpaid"),
    PARTIALLY_PAID("Partially Paid"),
    OVERDUE("Overdue"),
    PAID("Paid"),
    CANCELLED("Cancelled")
}