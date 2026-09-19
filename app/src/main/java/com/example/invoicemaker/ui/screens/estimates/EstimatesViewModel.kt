package com.example.invoicemaker.ui.screens.estimates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.local.InvoiceDatabase
import com.example.invoicemaker.data.local.entity.EstimateEntity
import com.example.invoicemaker.data.local.entity.EstimateItemEntity
import com.example.invoicemaker.data.local.entity.EstimateStatus
import com.example.invoicemaker.data.repository.ClientRepository // ASSUMPTION: exists, mirrors EstimateRepository
import com.example.invoicemaker.data.repository.EstimateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class EstimatesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = InvoiceDatabase.getInstance(application)

    private val estimateRepository = EstimateRepository(
        estimateDao = db.estimateDao(),
        estimateItemDao = db.estimateItemDao()
    )
    private val clientRepository = ClientRepository(clientDao = db.clientDao()) // ASSUMPTION: constructor shape

    val estimates: Flow<List<EstimateUiModel>> = combine(
        estimateRepository.observeAll(),
        clientRepository.observeAll(), // ASSUMPTION: method exists, mirrors EstimateRepository.observeAll()
        estimateRepository.observeAllItems()
    ) { estimateList, clients, allItems ->
        val clientNameById = clients.associateBy({ it.id }, { it.name }) // ASSUMPTION: ClientEntity has id, name
        val itemsByEstimateId = allItems.groupBy { it.estimateId }

        estimateList.map { estimate ->
            buildEstimateUiModel(
                estimate = estimate,
                clientName = clientNameById[estimate.clientId] ?: "Unknown Client",
                items = itemsByEstimateId[estimate.id].orEmpty()
            )
        }
    }

    fun deleteEstimate(id: Long) {
        viewModelScope.launch { estimateRepository.deleteById(id) }
    }

    fun updateStatus(id: Long, newStatus: EstimateStatus) {
        viewModelScope.launch { estimateRepository.updateStatus(id, newStatus.name) }
    }

    suspend fun generateNextEstimateNumber(): String {
        val last = estimateRepository.getLastEstimateNumber()
        val nextNumber = last?.substringAfterLast("-")?.toIntOrNull()?.plus(1) ?: 1
        return "EST-%04d".format(nextNumber)
    }
}

// --- Mapper (unchanged) ---

private fun buildEstimateUiModel(
    estimate: EstimateEntity,
    clientName: String,
    items: List<EstimateItemEntity>
): EstimateUiModel {
    val subtotal = items.fold(BigDecimal.ZERO) { acc, item -> acc + BigDecimal.valueOf(item.lineTotal) }
    val totalTax = items.fold(BigDecimal.ZERO) { acc, item ->
        acc + BigDecimal.valueOf(item.lineTotal)
            .multiply(BigDecimal.valueOf(item.taxRate))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
    }
    val total = subtotal + totalTax

    val status = EstimateStatus.entries.find { it.name == estimate.status } ?: EstimateStatus.DRAFT

    return EstimateUiModel(
        id = estimate.id,
        estimateNumber = estimate.estimateNumber,
        clientName = clientName,
        status = status,
        issueDate = estimate.issueDate,
        expiryDate = estimate.expiryDate,
        itemCount = items.size,
        subtotal = subtotal,
        totalTax = totalTax,
        total = total
    )
}