// data/mapper/LineItemMapper.kt
package com.example.invoicemaker.data.mapper

import com.example.invoicemaker.data.local.entity.EstimateItemEntity

fun `InvoiceLineEntity.kt`.toUiModel(currencySymbol: String): LineItem = LineItem(
    id = id,
    description = description,
    quantityLabel = "%.0f x %,.2f %s".format(quantity, unitPrice, currencySymbol),
    lineTotal = "%,.2f %s".format(lineTotal, currencySymbol)
)

fun EstimateItemEntity.toUiModel(currencySymbol: String): LineItem = LineItem(
    id = id,
    description = description,
    quantityLabel = "%.0f x %,.2f %s".format(quantity, unitPrice, currencySymbol),
    lineTotal = "%,.2f %s".format(lineTotal, currencySymbol)
)