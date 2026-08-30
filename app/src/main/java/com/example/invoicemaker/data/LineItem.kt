package com.example.invoicemaker.data

data class LineItem(
    val id: Long,
    val description: String,
    val quantityLabel: String,
    val lineTotal: String
)