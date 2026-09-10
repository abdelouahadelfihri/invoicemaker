package com.example.invoicemaker.data.mapper

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

// NOTE: subtotal/taxAmount/total are persisted below but NOT read back here —
// Estimate presumably recomputes them from lineItems. Kept as denormalized
// columns for now (useful if you ever need to query/sort by total without
// loading full line lists). If nothing queries these columns directly,
// consider dropping them from the entity entirely to remove the duplicate
// source of truth.
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

fun EstimateItemEntity.toDomain(): EstimateLine = EstimateLine(
    id = id,
    estimateId = estimateId,
    description = description,
    quantity = BigDecimal.valueOf(quantity),
    unit = runCatching { ItemUnit.valueOf(unit) }.getOrDefault(ItemUnit.UNIT),
    unitPrice = BigDecimal.valueOf(unitPrice),
    sortOrder = sortOrder
)

// NOTE: taxRate is hardcoded to 0.0 — EstimateLine has no taxRate field to
// read from. If per-line tax is a real requirement, add it to EstimateLine
// first; otherwise consider dropping the taxRate column from the entity.
// lineTotal is persisted but not read back in toDomain() (EstimateLine.total
// is presumably a computed property = quantity * unitPrice) — same
// denormalization tradeoff as the Estimate totals above.
fun EstimateLine.toEntity(): EstimateItemEntity = EstimateItemEntity(
    id = id,
    estimateId = estimateId,
    itemId = null,
    description = description,
    quantity = quantity.toDouble(),
    unit = unit.name,
    unitPrice = unitPrice.toDouble(),
    taxRate = 0.0,
    lineTotal = total.toDouble(),
    sortOrder = sortOrder
)