package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.Estimate
import com.example.invoicemaker.data.Item
import kotlinx.coroutines.flow.Flow

// interface — already inside ItemsViewModel.kt
interface EstimateDomainRepository {
    fun getAllEstimatesFlow(): Flow<List<Estimate>>
    suspend fun getEstimateById(id: Long): Estimate?
    suspend fun insertEstimate(estimate: Estimate): Long
    suspend fun updateEstimate(estimate: Estimate)
    suspend fun deleteEstimate(estimateId: Long)
    suspend fun getNextEstimateNumber(): String
}