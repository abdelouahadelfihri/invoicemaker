package com.example.invoicemaker.data.local.mapper

import com.example.invoicemaker.data.Item
import com.example.invoicemaker.data.ItemUnit
import com.example.invoicemaker.data.local.entity.ItemEntity
import java.math.BigDecimal

// NOTE: this mapping guesses at ItemUnit's enum constants and how they
// correspond to the raw strings Room stores ("pcs", "hr", "kg", "day").
// You have not shared the actual ItemUnit enum yet — once you do, replace
// the two functions below with an exact mapping to its real constant names.

fun ItemEntity.toDomain(): Item = Item(
    id = id,
    name = name,
    description = description,
    unit = unit.toItemUnit(),
    unitPrice = BigDecimal.valueOf(unitPrice),
    defaultTaxRate = BigDecimal.valueOf(taxRate),
    sku = sku,
    isActive = isActive,
    createdAt = createdAt
)

fun Item.toEntity(): ItemEntity = ItemEntity(
    id = id,
    name = name,
    description = description,
    sku = sku,
    unit = unit.toRawString(),
    unitPrice = unitPrice.toDouble(),
    taxRate = defaultTaxRate.toDouble(),
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis()
)

private fun String.toItemUnit(): ItemUnit = when (this) {
    "pcs" -> ItemUnit.UNIT
    "hr" -> ItemUnit.HOUR
    "kg" -> ItemUnit.KG
    "day" -> ItemUnit.DAY
    else -> ItemUnit.UNIT
}

private fun ItemUnit.toRawString(): String = when (this) {
    ItemUnit.UNIT -> "pcs"
    ItemUnit.HOUR -> "hr"
    ItemUnit.KG -> "kg"
    ItemUnit.DAY -> "day"
}