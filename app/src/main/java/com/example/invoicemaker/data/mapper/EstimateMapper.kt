// data/mapper/EstimateMapper.kt
package com.example.invoicemaker.data.mapper

import com.example.invoicemaker.data.local.entity.EstimateEntity
import com.example.invoicemaker.data.Estimate
import com.example.invoicemaker.data.EstimateStatus
import java.text.SimpleDateFormat
import java.util.*

fun EstimateEntity.toUiModel(clientName: String, currencySymbol: String): Estimate {
    val now = System.currentTimeMillis()
    val dateFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val status = when (this.status) {
        "CONVERTED" -> EstimateStatus.CONVERTED
        "ACCEPTED" -> EstimateStatus.ACCEPTED
        "DECLINED" -> EstimateStatus.DECLINED
        "SENT" -> if (expiryDate != null && now > expiryDate) EstimateStatus.EXPIRED else EstimateStatus.SENT
        else -> EstimateStatus.DRAFT
    }

    val expiryInfo = expiryDate?.let {
        val daysDiff = ((it - now) / 86_400_000L).toInt()
        if (daysDiff >= 0) "Expires in $daysDiff days" else "Expired"
    } ?: "No expiry"

    return Estimate(
        id = id,
        estimateNumber = estimateNumber,
        clientName = clientName,
        amount = "%,.2f %s".format(total, currencySymbol),
        issueDate = dateFmt.format(Date(issueDate)),
        expiryInfo = expiryInfo,
        status = status
    )
}