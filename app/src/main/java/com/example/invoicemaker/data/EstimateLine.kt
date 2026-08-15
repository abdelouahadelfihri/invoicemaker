package com.example.invoicemaker.data

import java.math.BigDecimal

data class EstimateLine(
    val id: Long = 0,
    val estimateId: Long = 0,           // FK -> Estimate.id, filled in when saved
    val itemId: Long? = null,           // FK -> Item.id, nullable if item later deleted
    val description: String,
    val quantity: BigDecimal,
    val unit: ItemUnit = ItemUnit.UNIT,
    val unitPrice: BigDecimal,
    val taxRate: BigDecimal = BigDecimal.ZERO,
    val discount: BigDecimal = BigDecimal.ZERO,
    val sortOrder: Int = 0
) {
    val subtotal: BigDecimal
        get() = unitPrice.multiply(quantity).subtract(discount)

    val taxAmount: BigDecimal
        get() = subtotal.multiply(taxRate).divide(BigDecimal(100))

    val total: BigDecimal
        get() = subtotal.add(taxAmount)
}