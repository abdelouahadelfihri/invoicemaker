package com.example.invoicemaker.ui.screens.clients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.local.InvoiceDatabase
import com.example.invoicemaker.data.local.entity.Client
import com.example.invoicemaker.data.repository.ClientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientDetailUiState(
    val client: Client? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

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

    private val _detailState = MutableStateFlow(ClientDetailUiState())
    val detailState: StateFlow<ClientDetailUiState> = _detailState.asStateFlow()

    fun startNewClient() {
        _detailState.value = ClientDetailUiState(
            client = Client(
                id = 0,
                name = "",
                email = "",
                phone = "",
                address = "",
                notes = ""
            )
        )
    }

    fun loadClient(id: Long) {
        viewModelScope.launch {
            val client = repository.getClientById(id)
            _detailState.value = ClientDetailUiState(client = client)
        }
    }

    fun updateName(value: String) =
        _detailState.update { it.copy(client = it.client?.copy(name = value)) }

    fun updateEmail(value: String) =
        _detailState.update { it.copy(client = it.client?.copy(email = value)) }

    fun updatePhone(value: String) =
        _detailState.update { it.copy(client = it.client?.copy(phone = value)) }

    fun updateAddress(value: String) =
        _detailState.update { it.copy(client = it.client?.copy(address = value)) }

    fun updateNotes(value: String) =
        _detailState.update { it.copy(client = it.client?.copy(notes = value)) }

    fun saveClient(onSaved: (Long) -> Unit) {
        val client = _detailState.value.client ?: return

        if (client.name.isBlank()) {
            _detailState.update { it.copy(errorMessage = "Name is required") }
            return
        }

        _detailState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                val id = if (client.id == 0L) {
                    repository.insertClient(client)   // must return the new row id (Long)
                } else {
                    repository.updateClient(client)
                    client.id
                }
                _detailState.update { it.copy(isSaving = false) }
                onSaved(id)
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "Failed to save client")
                }
            }
        }
    }

    fun clearError() {
        _detailState.update { it.copy(errorMessage = null) }
    }

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