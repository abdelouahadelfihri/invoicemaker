package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.Estimate
import com.example.invoicemaker.data.local.mapper.toDomain
import com.example.invoicemaker.data.local.mapper.toEntity
import com.example.invoicemaker.data.repository.EstimateDomainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EstimateRepositoryAdapter(
    private val estimateRepository: EstimateRepository
) : EstimateDomainRepository {

    override fun getAllEstimatesFlow(): Flow<List<Estimate>> =
        estimateRepository.observeAll().map { list ->
            list.map { entity -> entity.toDomain(estimateRepository.getItemsFor(entity.id)) }
        }

    override suspend fun getEstimateById(id: Long): Estimate? {
        val entity = estimateRepository.getById(id) ?: return null
        return entity.toDomain(estimateRepository.getItemsFor(id))
    }

    override suspend fun insertEstimate(estimate: Estimate): Long =
        estimateRepository.save(estimate.toEntity(), estimate.lineItems.map { it.toEntity() })

    override suspend fun updateEstimate(estimate: Estimate) {
        estimateRepository.save(estimate.toEntity(), estimate.lineItems.map { it.toEntity() })
    }

    override suspend fun deleteEstimate(estimateId: Long) =
        estimateRepository.deleteById(estimateId)

    override suspend fun getNextEstimateNumber(): String {
        val count = estimateRepository.getCount()
        return "EST-%04d".format(count + 1)
    }
}