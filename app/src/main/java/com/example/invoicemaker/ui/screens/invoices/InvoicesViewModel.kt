package com.example.invoicemaker.ui.screens.invoices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourpackage.invoicemaker.data.InvoiceStatus
import com.yourpackage.invoicemaker.data.local.AppDatabase
import com.yourpackage.invoicemaker.data.repository.ClientRepository
import com.yourpackage.invoicemaker.data.repository.InvoiceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.text.get

class InvoicesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val invoiceRepository = InvoiceRepository(db.invoiceDao())
    private val clientRepository = ClientRepository(db.clientDao())

    val invoices: StateFlow<List<InvoiceUiModel>> =
        combine(invoiceRepository.getAllInvoices(), clientRepository.getAllClients()) { invoices, clients ->
            val clientsById = clients.associateBy { it.id }
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