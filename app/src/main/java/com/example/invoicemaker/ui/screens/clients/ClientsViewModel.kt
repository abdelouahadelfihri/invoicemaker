package com.example.invoicemaker.ui.screens.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.Client
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

// ---------------------------------------------------------------------------
// Repository contract — implement against your Room DAO.
// ---------------------------------------------------------------------------

interface ClientDomainRepository {
    fun getAllClientsFlow(): Flow<List<Client>>
    suspend fun getClientById(id: Long): Client?
    suspend fun insertClient(client: Client): Long
    suspend fun updateClient(client: Client)
    suspend fun deleteClient(clientId: Long)
}

// ---------------------------------------------------------------------------
// List screen filter/sort options
// ---------------------------------------------------------------------------

enum class ClientSortOrder { NAME_ASC, NAME_DESC, NEWEST_FIRST, OLDEST_FIRST }

data class ClientFilter(
    val query: String = "",
    val sortOrder: ClientSortOrder = ClientSortOrder.NAME_ASC
)

// ---------------------------------------------------------------------------
// Detail/edit screen state
// ---------------------------------------------------------------------------

data class ClientDetailState(
    val client: Client? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

class ClientsViewModel(
    private val clientRepository: ClientRepository
) : ViewModel() {

    // ---- LIST SCREEN -------------------------------------------------

    private val _filter = MutableStateFlow(ClientFilter())
    val filter: StateFlow<ClientFilter> = _filter.asStateFlow()

    /** Drives `viewModel.clients.collectAsState(initial = emptyList())` in Compose. */
    val clients: StateFlow<List<Client>> = combine(
        clientRepository.getAllClientsFlow(),
        _filter
    ) { clientList, filter ->
        clientList
            .asSequence()
            .filter { client ->
                if (filter.query.isBlank()) return@filter true
                client.name.contains(filter.query, ignoreCase = true) ||
                        client.email?.contains(filter.query, ignoreCase = true) == true ||
                        client.phone?.contains(filter.query, ignoreCase = true) == true
            }
            .sortedWith(
                when (filter.sortOrder) {
                    ClientSortOrder.NAME_ASC -> compareBy { it.name.lowercase() }
                    ClientSortOrder.NAME_DESC -> compareByDescending { it.name.lowercase() }
                    ClientSortOrder.NEWEST_FIRST -> compareByDescending { it.createdAt }
                    ClientSortOrder.OLDEST_FIRST -> compareBy { it.createdAt }
                }
            )
            .toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _filter.update { it.copy(query = query) }
    }

    fun setSortOrder(order: ClientSortOrder) {
        _filter.update { it.copy(sortOrder = order) }
    }

    // ---- DETAIL / EDIT SCREEN -----------------------------------------

    private val _detailState = MutableStateFlow(ClientDetailState())
    val detailState: StateFlow<ClientDetailState> = _detailState.asStateFlow()

    fun loadClient(id: Long) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val client = clientRepository.getClientById(id)
                _detailState.update { it.copy(client = client, isLoading = false) }
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load client")
                }
            }
        }
    }

    fun startNewClient() {
        _detailState.value = ClientDetailState(client = Client(name = ""))
    }

    fun updateName(name: String) {
        _detailState.update { state ->
            state.client?.let { state.copy(client = it.copy(name = name)) } ?: state
        }
    }

    fun updateEmail(email: String) {
        _detailState.update { state ->
            state.client?.let { state.copy(client = it.copy(email = email.ifBlank { null })) } ?: state
        }
    }

    fun updatePhone(phone: String) {
        _detailState.update { state ->
            state.client?.let { state.copy(client = it.copy(phone = phone.ifBlank { null })) } ?: state
        }
    }

    fun updateAddress(address: String) {
        _detailState.update { state ->
            state.client?.let { state.copy(client = it.copy(address = address.ifBlank { null })) } ?: state
        }
    }

    fun updateNotes(notes: String) {
        _detailState.update { state ->
            state.client?.let { state.copy(client = it.copy(notes = notes.ifBlank { null })) } ?: state
        }
    }

    // ---- Save / delete --------------------------------------------------

    fun saveClient(onSaved: (Long) -> Unit = {}) {
        val client = _detailState.value.client ?: return
        if (client.name.isBlank()) {
            _detailState.update { it.copy(errorMessage = "Client name is required") }
            return
        }

        viewModelScope.launch {
            _detailState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val clientId = if (client.id == 0L) {
                    clientRepository.insertClient(client)
                } else {
                    clientRepository.updateClient(client)
                    client.id
                }
                _detailState.update { it.copy(isSaving = false) }
                onSaved(clientId)
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "Failed to save client")
                }
            }
        }
    }

    fun deleteClient(id: Long, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                clientRepository.deleteClient(id)
                onDeleted()
            } catch (e: Exception) {
                _detailState.update { it.copy(errorMessage = e.message ?: "Failed to delete client") }
            }
        }
    }

    fun deleteClientFromList(id: Long) {
        viewModelScope.launch { clientRepository.deleteClient(id) }
    }

    fun clearError() {
        _detailState.update { it.copy(errorMessage = null) }
    }

    fun resetDetailState() {
        _detailState.value = ClientDetailState()
    }

    companion object {
        fun factory(clientRepository: ClientDomainRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { ClientsViewModel(clientRepository) }
            }
    }
}