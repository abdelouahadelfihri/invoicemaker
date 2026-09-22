package com.example.invoicemaker.ui.screens.clients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.local.InvoiceDatabase
import com.example.invoicemaker.data.local.entity.Client
import com.example.invoicemaker.data.repository.ClientRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClientsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClientRepository =
        ClientRepository(InvoiceDatabase.getInstance(application).clientDao())

    val clients: StateFlow<List<Client>> =
        repository.observeAllClients()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun insertClient(client: Client) {
        viewModelScope.launch { repository.insertClient(client) }
    }

    fun updateClient(client: Client) {
        viewModelScope.launch { repository.updateClient(client) }
    }

    fun deleteClient(id: Long) {
        viewModelScope.launch { repository.deleteClient(id) }
    }
}