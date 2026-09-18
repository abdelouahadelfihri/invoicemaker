package com.example.invoicemaker.ui.screens.estimates

import com.example.invoicemaker.data.local.entity.EstimateStatus
import java.math.BigDecimal

data class EstimateUiModel(
    val id: Long,
    val estimateNumber: String,
    val clientName: String,
    val status: EstimateStatus,
    val issueDate: Long,
    val expiryDate: Long,
    val itemCount: Int,
    val subtotal: BigDecimal,
    val totalTax: BigDecimal,
    val total: BigDecimal
)