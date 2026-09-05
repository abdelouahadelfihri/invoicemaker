package com.example.invoicemaker.ui.screens.invoices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.InvoiceStatus
import com.example.invoicemaker.data.local.AppDatabase
import com.example.invoicemaker.data.local.entity.toDomain
import com.example.invoicemaker.data.repository.ClientRepository
import com.example.invoicemaker.data.repository.InvoiceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InvoicesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val invoiceRepository = InvoiceRepository(db.invoiceDao())
    private val clientRepository = ClientRepository(db.clientDao())

    val invoices: StateFlow<List<InvoiceUiModel>> =
        combine(invoiceRepository.observeAll(), clientRepository.observeAll()) { invoices, clientEntities ->
            val clientsById = clientEntities.associate { it.id to it.toDomain() }
            invoices.map { invoice -> invoice.toUiModel(clientsById[invoice.clientId]) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteInvoice(invoiceId: Long) {
        viewModelScope.launch { invoiceRepository.deleteInvoice(invoiceId) }
    }

    fun markAsPaid(invoiceId: Long) {
        viewModelScope.launch { invoiceRepository.updateStatus(invoiceId, InvoiceStatus.PAID) }
    }
}