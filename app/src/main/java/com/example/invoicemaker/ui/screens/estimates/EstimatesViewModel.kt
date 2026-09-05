package com.example.invoicemaker.ui.screens.estimates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.Client
import com.example.invoicemaker.data.Estimate
import com.example.invoicemaker.data.EstimateLine
import com.example.invoicemaker.data.EstimateStatus
import com.example.invoicemaker.data.ItemUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

// ---------------------------------------------------------------------------
// computedStatus — put this next to the Estimate data class in your data
// package. Auto-flips DRAFT/SENT to EXPIRED once expiryDate has passed,
// but never overrides a manually-set ACCEPTED / REJECTED / CONVERTED status.
// ---------------------------------------------------------------------------

val Estimate.computedStatus: EstimateStatus
    get() {
        if (status == EstimateStatus.ACCEPTED ||
            status == EstimateStatus.REJECTED ||
            status == EstimateStatus.CONVERTED
        ) return status
        return if (System.currentTimeMillis() > expiryDate) EstimateStatus.EXPIRED else status
    }

// ---------------------------------------------------------------------------
// Repository contracts — implement against your Room DAO.
// Persisting lineItems (embedded list) alongside the Estimate row is an
// implementation detail of insertEstimate/updateEstimate (e.g. a TypeConverter,
// or the DAO writing to a child table internally) — the ViewModel just passes
// the whole Estimate object through.
// ---------------------------------------------------------------------------

interface EstimateRepository {
    fun getAllEstimatesFlow(): Flow<List<Estimate>>
    suspend fun getEstimateById(id: Long): Estimate?
    suspend fun insertEstimate(estimate: Estimate): Long
    suspend fun updateEstimate(estimate: Estimate)
    suspend fun deleteEstimate(estimateId: Long)
    suspend fun getNextEstimateNumber(): String
}

interface ClientRepository {
    fun getAllClientsFlow(): Flow<List<Client>>
    suspend fun getClientById(id: Long): Client?
}

// ---------------------------------------------------------------------------
// List screen filter options
// ---------------------------------------------------------------------------

enum class EstimateSortOrder { DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC }

data class EstimateFilter(
    val query: String = "",
    val statusFilter: EstimateStatus? = null,
    val sortOrder: EstimateSortOrder = EstimateSortOrder.DATE_DESC
)

// ---------------------------------------------------------------------------
// Detail/edit screen state — wraps the working Estimate directly, since
// lineItems already live inside it.
// ---------------------------------------------------------------------------

data class EstimateDetailState(
    val estimate: Estimate? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

class EstimatesViewModel(

    private val estimateRepository: EstimateRepository,
    private val clientRepository: ClientRepository

) : ViewModel() {

    // ---- LIST SCREEN -------------------------------------------------

    private val _filter = MutableStateFlow(EstimateFilter())
    val filter: StateFlow<EstimateFilter> = _filter.asStateFlow()

    /** Drives `viewModel.estimates.collectAsState(initial = emptyList())` in Compose. */
    val estimates: StateFlow<List<EstimateUiModel>> = combine(
        estimateRepository.getAllEstimatesFlow(),
        clientRepository.getAllClientsFlow(),
        _filter
    ) { estimateList, clients, filter ->
        val clientsById = clients.associateBy { it.id }

        estimateList
            .asSequence()
            .filter { est ->
                filter.statusFilter == null || est.computedStatus == filter.statusFilter
            }
            .filter { est ->
                if (filter.query.isBlank()) return@filter true
                val client = clientsById[est.clientId]
                est.estimateNumber.contains(filter.query, ignoreCase = true) ||
                        client?.name?.contains(filter.query, ignoreCase = true) == true
            }
            .sortedWith(
                when (filter.sortOrder) {
                    EstimateSortOrder.DATE_DESC -> compareByDescending { it.issueDate }
                    EstimateSortOrder.DATE_ASC -> compareBy { it.issueDate }
                    EstimateSortOrder.AMOUNT_DESC -> compareByDescending { it.total }
                    EstimateSortOrder.AMOUNT_ASC -> compareBy { it.total }
                }
            )
            .map { est -> est.toUiModel(clientsById[est.clientId]) }
            .toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _filter.update { it.copy(query = query) }
    }

    fun setStatusFilter(status: EstimateStatus?) {
        _filter.update { it.copy(statusFilter = status) }
    }

    fun setSortOrder(order: EstimateSortOrder) {
        _filter.update { it.copy(sortOrder = order) }
    }

    // ---- DETAIL / EDIT SCREEN -----------------------------------------

    private val _detailState = MutableStateFlow(EstimateDetailState())
    val detailState: StateFlow<EstimateDetailState> = _detailState.asStateFlow()

    fun loadEstimate(id: Long) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val estimate = estimateRepository.getEstimateById(id)
                _detailState.update { it.copy(estimate = estimate, isLoading = false) }
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load estimate")
                }
            }
        }
    }

    /** Call when creating a brand-new estimate (e.g. tapping "+" on the list screen). */
    fun startNewEstimate(clientId: Long) {
        viewModelScope.launch {
            val number = estimateRepository.getNextEstimateNumber()
            _detailState.value = EstimateDetailState(
                estimate = Estimate(
                    id = 0,
                    estimateNumber = number,
                    clientId = clientId,
                    lineItems = emptyList(),
                    status = EstimateStatus.DRAFT,
                    issueDate = System.currentTimeMillis(),
                    expiryDate = System.currentTimeMillis() + THIRTY_DAYS_MS
                )
            )
        }
    }

    fun updateClient(clientId: Long) {
        _detailState.update { state ->
            state.estimate?.let { state.copy(estimate = it.copy(clientId = clientId)) } ?: state
        }
    }

    fun updateExpiryDate(timestamp: Long) {
        _detailState.update { state ->
            state.estimate?.let { state.copy(estimate = it.copy(expiryDate = timestamp)) } ?: state
        }
    }

    fun updateNotes(notes: String) {
        _detailState.update { state ->
            state.estimate?.let { state.copy(estimate = it.copy(notes = notes)) } ?: state
        }
    }

    fun updateTerms(terms: String) {
        _detailState.update { state ->
            state.estimate?.let { state.copy(estimate = it.copy(termsAndConditions = terms)) } ?: state
        }
    }

    fun updateStatus(status: EstimateStatus) {
        _detailState.update { state ->
            state.estimate?.let { state.copy(estimate = it.copy(status = status)) } ?: state
        }
    }

    // ---- Line items (embedded in the Estimate) ------------------------

    fun addLine() {
        _detailState.update { state ->
            val estimate = state.estimate ?: return@update state
            val newLine = EstimateLine(
                estimateId = estimate.id,
                description = "",
                quantity = BigDecimal.ONE,
                unit = ItemUnit.UNIT,
                unitPrice = BigDecimal.ZERO,
                sortOrder = estimate.lineItems.size
            )
            state.copy(estimate = estimate.copy(lineItems = estimate.lineItems + newLine))
        }
    }

    fun updateLine(lineId: Long, transform: (EstimateLine) -> EstimateLine) {
        _detailState.update { state ->
            val estimate = state.estimate ?: return@update state
            val updatedLines = estimate.lineItems.map { if (it.id == lineId) transform(it) else it }
            state.copy(estimate = estimate.copy(lineItems = updatedLines))
        }
    }

    fun removeLine(lineId: Long) {
        _detailState.update { state ->
            val estimate = state.estimate ?: return@update state
            state.copy(estimate = estimate.copy(lineItems = estimate.lineItems.filterNot { it.id == lineId }))
        }
    }

    // ---- Save / delete --------------------------------------------------

    fun saveEstimate(onSaved: (Long) -> Unit = {}) {
        val estimate = _detailState.value.estimate ?: return

        viewModelScope.launch {
            _detailState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val estimateId = if (estimate.id == 0L) {
                    estimateRepository.insertEstimate(estimate)
                } else {
                    estimateRepository.updateEstimate(estimate)
                    estimate.id
                }
                _detailState.update { it.copy(isSaving = false) }
                onSaved(estimateId)
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "Failed to save estimate")
                }
            }
        }
    }

    fun deleteEstimate(id: Long, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                estimateRepository.deleteEstimate(id)
                onDeleted()
            } catch (e: Exception) {
                _detailState.update { it.copy(errorMessage = e.message ?: "Failed to delete estimate") }
            }
        }
    }

    fun deleteEstimateFromList(id: Long) {
        viewModelScope.launch { estimateRepository.deleteEstimate(id) }
    }

    // ---- Quick status actions from the list screen -----------------------

    fun markAsSent(id: Long) = updateStatusById(id, EstimateStatus.SENT)
    fun markAsAccepted(id: Long) = updateStatusById(id, EstimateStatus.ACCEPTED)
    fun markAsRejected(id: Long) = updateStatusById(id, EstimateStatus.REJECTED)

    private fun updateStatusById(id: Long, status: EstimateStatus) {
        viewModelScope.launch {
            estimateRepository.getEstimateById(id)?.let {
                estimateRepository.updateEstimate(it.copy(status = status))
            }
        }
    }

    /**
     * Convert an accepted estimate into an invoice. Marks the estimate as
     * CONVERTED and records the new invoice's id. Hook the actual Invoice
     * creation (mapping lineItems -> InvoiceLine) up to your InvoiceRepository
     * before calling this, then pass in the resulting invoice id.
     */
    fun markConverted(estimateId: Long, invoiceId: Long) {
        viewModelScope.launch {
            estimateRepository.getEstimateById(estimateId)?.let {
                estimateRepository.updateEstimate(
                    it.copy(status = EstimateStatus.CONVERTED, convertedInvoiceId = invoiceId)
                )
            }
        }
    }

    fun clearError() {
        _detailState.update { it.copy(errorMessage = null) }
    }

    fun resetDetailState() {
        _detailState.value = EstimateDetailState()
    }

    companion object {
        private const val THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000
    }
}