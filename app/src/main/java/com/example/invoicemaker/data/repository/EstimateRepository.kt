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
    suspend fun getById(id: Long): EstimateEntity? = estimateDao.getById(id)
    suspend fun getItemsFor(estimateId: Long): List<EstimateItemEntity> =
        estimateItemDao.getForEstimate(estimateId)
    suspend fun getCount(): Int = estimateDao.getCount()

    suspend fun save(estimate: EstimateEntity, items: List<EstimateItemEntity>): Long {
        val id = if (estimate.id == 0L) estimateDao.insert(estimate) else {
            estimateDao.update(estimate)
            estimate.id
        }
        estimateItemDao.deleteForEstimate(id)
        estimateItemDao.insertAll(items.map { it.copy(estimateId = id) })
        return id
    }

    suspend fun deleteById(id: Long) {
        getById(id)?.let { estimateDao.delete(it) }
    }
}