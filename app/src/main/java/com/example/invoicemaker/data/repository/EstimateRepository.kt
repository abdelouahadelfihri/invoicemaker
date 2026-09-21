package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.local.dao.EstimateDao
import com.example.invoicemaker.data.local.dao.EstimateItemDao
import com.example.invoicemaker.data.local.entity.EstimateEntity
import com.example.invoicemaker.data.local.entity.EstimateItemEntity
import kotlinx.coroutines.flow.Flow

class EstimateRepository(
    private val estimateDao: EstimateDao,
    private val estimateItemDao: EstimateItemDao
) {
    fun observeAll(): Flow<List<EstimateEntity>> = estimateDao.observeAll()

    // --- New: needed by EstimatesViewModel to build UI models with item totals ---
    fun observeAllItems(): Flow<List<EstimateItemEntity>> = estimateItemDao.observeAll()

    suspend fun getById(id: Long): EstimateEntity? = estimateDao.getById(id)
    suspend fun getItemsFor(estimateId: Long): List<EstimateItemEntity> =
        estimateItemDao.getForEstimate(estimateId)
    suspend fun getCount(): Int = estimateDao.getCount()

    // --- New: needed for generateNextEstimateNumber() ---
    suspend fun getLastEstimateNumber(): String? = estimateDao.getLastEstimateNumber()

    suspend fun save(estimate: EstimateEntity, items: List<EstimateItemEntity>): Long {
        val id = if (estimate.id == 0L) estimateDao.insert(estimate) else {
            estimateDao.update(estimate)
            estimate.id
        }
        estimateItemDao.deleteAllForEstimate(id)
        estimateItemDao.insertAll(items.map { it.copy(estimateId = id) })
        return id
    }

    // --- New: needed for updateStatus() in ViewModel ---
    suspend fun updateStatus(id: Long, status: String) {
        val estimate = getById(id) ?: return
        estimateDao.update(estimate.copy(status = status))
    }

    suspend fun deleteById(id: Long) {
        getById(id)?.let { estimateDao.delete(it) }
    }
}