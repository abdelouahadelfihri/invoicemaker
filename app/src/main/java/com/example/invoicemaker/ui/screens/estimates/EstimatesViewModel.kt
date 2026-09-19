package com.example.invoicemaker.ui.screens.invoices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.local.InvoiceDatabase
import com.example.invoicemaker.data.local.entity.EstimateEntity
import com.example.invoicemaker.data.local.entity.EstimateItemEntity
import com.example.invoicemaker.data.repository.EstimateRepository
import com.example.invoicemaker.data.repository.ClientRepository // ASSUMPTION: same as InvoicesViewModel
import com.example.invoicemaker.data.local.entity.EstimateStatus
import com.example.invoicemaker.ui.screens.estimates.EstimateUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class EstimatesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = InvoiceDatabase.getInstance(application)

    // ASSUMPTION: EstimateRepository constructor shape mirrors InvoiceRepository's
    private val estimateRepository = EstimateRepository(
        estimateDao = db.estimateDao(),
        estimateItemDao = db.estimateItemDao()
    )
    private val clientRepository = ClientRepository(clientDao = db.clientDao())

    val estimates: Flow<List<EstimateUiModel>> = combine(
        estimateRepository.observeAll(),
        clientRepository.observeAll()
    ) { estimateList, clients ->
        val clientNameById = clients.associateBy({ it.id }, { it.name })

        estimateList.map { estimate ->
            // ASSUMPTION: EstimateRepository.getItemsFor(id) is a suspend fun,
            // not a Flow — if it IS a Flow, this needs its own combine() instead.
            val items = estimateRepository.getItemsFor(estimate.id)
            buildEstimateUiModel(
                estimate = estimate,
                clientName = clientNameById[estimate.clientId] ?: "Unknown Client",
                items = items
            )
        }
    }

    fun deleteEstimate(id: Long) {
        viewModelScope.launch { estimateRepository.deleteById(id) }
    }

    suspend fun generateNextEstimateNumber(): String {
        val count = estimateRepository.getCount()
        return "EST-%04d".format(count + 1)
    }
}

private fun buildEstimateUiModel(
    estimate: EstimateEntity,
    clientName: String,
    items: List<EstimateItemEntity>
): EstimateUiModel {
    val subtotal = items.fold(BigDecimal.ZERO) { acc, item -> acc + BigDecimal.valueOf(item.lineTotal) }
    val totalTax = items.fold(BigDecimal.ZERO) { acc, item ->
        val lineTax = BigDecimal.valueOf(item.lineTotal)
            .multiply(BigDecimal.valueOf(item.taxRate))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        acc + lineTax
    }
    val total = subtotal + totalTax

    val status = EstimateStatus.entries.find { it.name == estimate.status } ?: EstimateStatus.DRAFT

    return EstimateUiModel(
        id = estimate.id,
        estimateNumber = estimate.estimateNumber, // ASSUMPTION: field name
        clientName = clientName,
        status = status,
        issueDate = estimate.issueDate,
        expiryDate = estimate.expiryDate, // ASSUMPTION: field name
        itemCount = items.size,
        subtotal = subtotal,
        totalTax = totalTax,
        total = total
    )
}