package com.example.invoicemaker.data.local.mapper

import com.example.invoicemaker.data.*
import com.example.invoicemaker.data.local.entity.EstimateEntity
import com.example.invoicemaker.data.local.entity.EstimateItemEntity
import java.math.BigDecimal

fun EstimateEntity.toDomain(lines: List<EstimateItemEntity>): Estimate = Estimate(
    id = id,
    estimateNumber = estimateNumber,
    clientId = clientId,
    lineItems = lines.map { it.toDomain() },
    status = EstimateStatus.valueOf(status),
    issueDate = issueDate,
    expiryDate = expiryDate ?: issueDate,
    notes = notes,
    termsAndConditions = terms,
    convertedInvoiceId = convertedInvoiceId,
    createdAt = createdAt
)

fun Estimate.toEntity(): EstimateEntity = EstimateEntity(
    id = id,
    estimateNumber = estimateNumber,
    clientId = clientId,
    issueDate = issueDate,
    expiryDate = expiryDate,
    status = status.name,
    subtotal = subtotal.toDouble(),
    taxAmount = totalTax.toDouble(),
    total = total.toDouble(),
    notes = notes,
    terms = termsAndConditions,
    convertedInvoiceId = convertedInvoiceId,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis()
)

// NOTE: EstimateItemEntity has no `unit` column, so this defaults to
// ItemUnit.UNIT on every read. Add a `unit: String` column to
// EstimateItemEntity if you need per-line units preserved.
fun EstimateItemEntity.toDomain(): EstimateLine = EstimateLine(
    id = id,
    estimateId = estimateId,
    description = description,
    quantity = BigDecimal.valueOf(quantity),
    unit = ItemUnit.UNIT,
    unitPrice = BigDecimal.valueOf(unitPrice),
    sortOrder = sortOrder
)

fun EstimateLine.toEntity(): EstimateItemEntity = EstimateItemEntity(
    id = id,
    estimateId = estimateId,
    itemId = null,
    description = description,
    quantity = quantity.toDouble(),
    unitPrice = unitPrice.toDouble(),
    taxRate = 0.0,
    lineTotal = total.toDouble(),
    sortOrder = sortOrder
)