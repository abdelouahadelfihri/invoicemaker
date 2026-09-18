package com.example.invoicemaker.data.local.entity

enum class InvoiceStatus(val label: String) {
    UNPAID("Unpaid"),
    PARTIALLY_PAID("Partially Paid"),
    OVERDUE("Overdue"),
    PAID("Paid"),
    CANCELLED("Cancelled")
}