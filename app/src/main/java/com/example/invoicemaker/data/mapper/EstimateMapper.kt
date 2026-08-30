package com.example.invoicemaker.data.mapper

import com.example.invoicemaker.data.Estimate
import com.example.invoicemaker.data.EstimateLine
import com.example.invoicemaker.data.EstimateStatus
import com.example.invoicemaker.data.local.entity.EstimateEntity
import com.example.invoicemaker.data.local.entity.EstimateItemEntity
import java.math.BigDecimal

fun EstimateItemEntity.toDomain(): EstimateLine = EstimateLine(
    id = id,
    estimateId = estimateId,
    itemId = itemId,
    description = description,
    quantity = BigDecimal(quantity),
    unitPrice = BigDecimal(unitPrice),
    taxRate = BigDecimal(taxRate),
    sortOrder = sortOrder
    // unit and discount aren't stored on EstimateItemEntity yet —
    // add columns for them there if you need per-line unit/discount persisted.
)

fun EstimateEntity.toDomain(lines: List<EstimateItemEntity>): Estimate {
    val now = System.currentTimeMillis()
    val status = when (this.status) {
        "SENT" -> if (expiryDate != null && now > expiryDate) EstimateStatus.EXPIRED else EstimateStatus.SENT
        "ACCEPTED" -> EstimateStatus.ACCEPTED
        "DECLINED" -> EstimateStatus.REJECTED
        "CONVERTED" -> EstimateStatus.CONVERTED
        "EXPIRED" -> EstimateStatus.EXPIRED
        else -> EstimateStatus.DRAFT
    }

    return Estimate(
        id = id,
        estimateNumber = estimateNumber,
        clientId = clientId,
        lineItems = lines.map { it.toDomain() },
        status = status,
        issueDate = issueDate,
        expiryDate = expiryDate ?: 0L,
        notes = notes,
        termsAndConditions = terms,
        convertedInvoiceId = convertedInvoiceId,
        createdAt = createdAt
    )
}