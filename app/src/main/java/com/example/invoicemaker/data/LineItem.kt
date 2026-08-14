package com.example.invoicemaker.data

data class LineItem(
    val id: Long,
    val description: String,
    val quantityLabel: String,   // "3 x 25.00 MAD"
    val lineTotal: String        // "75.00 MAD"
)