package com.example.invoicemaker.data

import java.math.BigDecimal

data class Item(
    val id: Long = 0,
    val name: String,                      // e.g. "Steel Beam IPE 200"
    val description: String? = null,
    val unit: ItemUnit = ItemUnit.UNIT,
    val unitPrice: BigDecimal,              // BigDecimal, matching your earlier float→BigDecimal fix
    val defaultTaxRate: BigDecimal = BigDecimal("20.00"), // VAT %, matches your CurrencyManager defaults
    val sku: String? = null,               // optional internal reference code
    val isActive: Boolean = true,          // soft-delete flag instead of hard delete
    val createdAt: Long = System.currentTimeMillis()
)