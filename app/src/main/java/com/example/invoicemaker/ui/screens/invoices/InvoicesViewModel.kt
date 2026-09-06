package com.example.invoicemaker.ui.screens.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.Client
import com.example.invoicemaker.data.InvoiceLine
import com.yourpackage.metalconstructions.data.Invoice
import com.yourpackage.metalconstructions.data.InvoiceStatus
import com.yourpackage.metalconstructions.data.Payment
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
// Invoice -> InvoiceUiModel mapping. InvoiceUiModel is the data class
// defined in InvoicesScreen.kt. Uses computedStatus (already built into
// Invoice), not the stored `status`, so Overdue/Paid/Partially Paid always
// reflect real payment state. If your Client class doesn't use `name`,
// adjust that one line.
// ---------------------------------------------------------------------------

fun Invoice.toUiModel(client: Client?): InvoiceUiModel = InvoiceUiModel(
    id = id,
    invoiceNumber = invoiceNumber,
    clientName = client?.name ?: "Unknown Client",
    status = computedStatus,
    issueDate = issueDate,
    dueDate = dueDate,
    itemCount = lineItems.size,
    paymentCount = payments.size,
    subtotal = subtotal,
    totalTax = totalTax,
    total = total,
    amountPaid = amountPaid,
    amountDue = amountDue
)

// ---------------------------------------------------------------------------
// Repository contracts — implement against your Room DAOs.
// Persisting lineItems/payments (embedded lists) alongside the Invoice row
// is an implementation detail of insertInvoice/updateInvoice — the
// ViewModel just passes the whole Invoice object through.
// ---------------------------------------------------------------------------

interface InvoiceRepository {
    fun getAllInvoicesFlow(): Flow<List<Invoice>>
    suspend fun getInvoiceById(id: Long): Invoice?
    suspend fun insertInvoice(invoice: Invoice): Long
    suspend fun updateInvoice(invoice: Invoice)
    suspend fun deleteInvoice(invoiceId: Long)
    suspend fun getNextInvoiceNumber(): String
}

interface InvoiceClientRepository {
    fun getAllClientsFlow(): Flow<List<Client>>
    suspend fun getClientById(id: Long): Client?
}

// ---------------------------------------------------------------------------
// List screen filter options
// ---------------------------------------------------------------------------

enum class InvoiceSortOrder { DATE_DESC, DATE_ASC, DUE_DATE_ASC, AMOUNT_DESC, AMOUNT_ASC, AMOUNT_DUE_DESC }

data class InvoiceFilter(
    val query: String = "",
    val statusFilter: InvoiceStatus? = null,
    val sortOrder: InvoiceSortOrder = InvoiceSortOrder.DATE_DESC
)

// ---------------------------------------------------------------------------
// Detail/edit screen state — wraps the working Invoice directly, since
// lineItems and payments already live inside it.
// ---------------------------------------------------------------------------

data class InvoiceDetailState(
    val invoice: Invoice? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

class InvoicesViewModel(

    private val invoiceRepository: InvoiceRepository,
    private val clientRepository: InvoiceClientRepository

) : ViewModel() {

    // ---- LIST SCREEN -------------------------------------------------

    private val _filter = MutableStateFlow(InvoiceFilter())
    val filter: StateFlow<InvoiceFilter> = _filter.asStateFlow()

    /** Drives `viewModel.invoices.collectAsState(initial = emptyList())` in Compose. */
    val invoices: StateFlow<List<InvoiceUiModel>> = combine(
        invoiceRepository.getAllInvoicesFlow(),
        clientRepository.getAllClientsFlow(),
        _filter
    ) { invoiceList, clients, filter ->
        val clientsById = clients.associateBy { it.id }

        invoiceList
            .asSequence()
            .filter { inv ->
                filter.statusFilter == null || inv.computedStatus == filter.statusFilter
            }
            .filter { inv ->
                if (filter.query.isBlank()) return@filter true
                val client = clientsById[inv.clientId]
                inv.invoiceNumber.contains(filter.query, ignoreCase = true) ||
                        client?.name?.contains(filter.query, ignoreCase = true) == true
            }
            .sortedWith(
                when (filter.sortOrder) {
                    InvoiceSortOrder.DATE_DESC -> compareByDescending { it.issueDate }
                    InvoiceSortOrder.DATE_ASC -> compareBy { it.issueDate }
                    InvoiceSortOrder.DUE_DATE_ASC -> compareBy { it.dueDate }
                    InvoiceSortOrder.AMOUNT_DESC -> compareByDescending { it.total }
                    InvoiceSortOrder.AMOUNT_ASC -> compareBy { it.total }
                    InvoiceSortOrder.AMOUNT_DUE_DESC -> compareByDescending { it.amountDue }
                }
            )
            .map { inv -> inv.toUiModel(clientsById[inv.clientId]) }
            .toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _filter.update { it.copy(query = query) }
    }

    fun setStatusFilter(status: InvoiceStatus?) {
        _filter.update { it.copy(statusFilter = status) }
    }

    fun setSortOrder(order: InvoiceSortOrder) {
        _filter.update { it.copy(sortOrder = order) }
    }

    // ---- DETAIL / EDIT SCREEN -----------------------------------------

    private val _detailState = MutableStateFlow(InvoiceDetailState())
    val detailState: StateFlow<InvoiceDetailState> = _detailState.asStateFlow()

    fun loadInvoice(id: Long) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val invoice = invoiceRepository.getInvoiceById(id)
                _detailState.update { it.copy(invoice = invoice, isLoading = false) }
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load invoice")
                }
            }
        }
    }

    /** Call when creating a brand-new invoice from scratch (tapping "+" on the list screen). */
    fun startNewInvoice(clientId: Long) {
        viewModelScope.launch {
            val number = invoiceRepository.getNextInvoiceNumber()
            _detailState.value = InvoiceDetailState(
                invoice = Invoice(
                    id = 0,
                    invoiceNumber = number,
                    clientId = clientId,
                    lineItems = emptyList(),
                    payments = emptyList(),
                    status = InvoiceStatus.UNPAID,
                    issueDate = System.currentTimeMillis(),
                    dueDate = System.currentTimeMillis() + THIRTY_DAYS_MS
                )
            )
        }
    }

    /**
     * Call when converting an accepted Estimate into an Invoice. Pass in the
     * mapped InvoiceLines (Estimate.lineItems -> InvoiceLine) and the source
     * estimate's id so it's tracked via sourceEstimateId.
     */
    fun startInvoiceFromEstimate(
        clientId: Long,
        sourceEstimateId: Long,
        mappedLineItems: List<InvoiceLine>
    ) {
        viewModelScope.launch {
            val number = invoiceRepository.getNextInvoiceNumber()
            _detailState.value = InvoiceDetailState(
                invoice = Invoice(
                    id = 0,
                    invoiceNumber = number,
                    clientId = clientId,
                    lineItems = mappedLineItems,
                    payments = emptyList(),
                    status = InvoiceStatus.UNPAID,
                    issueDate = System.currentTimeMillis(),
                    dueDate = System.currentTimeMillis() + THIRTY_DAYS_MS,
                    sourceEstimateId = sourceEstimateId
                )
            )
        }
    }

    fun updateClient(clientId: Long) {
        _detailState.update { state ->
            state.invoice?.let { state.copy(invoice = it.copy(clientId = clientId)) } ?: state
        }
    }

    fun updateDueDate(timestamp: Long) {
        _detailState.update { state ->
            state.invoice?.let { state.copy(invoice = it.copy(dueDate = timestamp)) } ?: state
        }
    }

    fun updateNotes(notes: String) {
        _detailState.update { state ->
            state.invoice?.let { state.copy(invoice = it.copy(notes = notes)) } ?: state
        }
    }

    fun updateStatus(status: InvoiceStatus) {
        _detailState.update { state ->
            state.invoice?.let { state.copy(invoice = it.copy(status = status)) } ?: state
        }
    }

    // ---- Line items (embedded in the Invoice) ------------------------

    fun addLine(newLine: InvoiceLine) {
        _detailState.update { state ->
            val invoice = state.invoice ?: return@update state
            state.copy(invoice = invoice.copy(lineItems = invoice.lineItems + newLine))
        }
    }

    fun updateLine(lineId: Long, transform: (InvoiceLine) -> InvoiceLine) {
        _detailState.update { state ->
            val invoice = state.invoice ?: return@update state
            val updatedLines = invoice.lineItems.map { if (it.id == lineId) transform(it) else it }
            state.copy(invoice = invoice.copy(lineItems = updatedLines))
        }
    }

    fun removeLine(lineId: Long) {
        _detailState.update { state ->
            val invoice = state.invoice ?: return@update state
            state.copy(invoice = invoice.copy(lineItems = invoice.lineItems.filterNot { it.id == lineId }))
        }
    }

    // ---- Payments (embedded in the Invoice) ----------------------------

    /** Records a payment against the currently-loaded invoice (in-memory; call saveInvoice() to persist). */
    fun addPayment(amount: BigDecimal, method: String? = null, note: String? = null) {
        _detailState.update { state ->
            val invoice = state.invoice ?: return@update state
            val payment = Payment(
                invoiceId = invoice.id,
                amount = amount,
                date = System.currentTimeMillis(),
                method = method,
                note = note
            )
            state.copy(invoice = invoice.copy(payments = invoice.payments + payment))
        }
    }

    fun removePayment(paymentId: Long) {
        _detailState.update { state ->
            val invoice = state.invoice ?: return@update state
            state.copy(invoice = invoice.copy(payments = invoice.payments.filterNot { it.id == paymentId }))
        }
    }

    /** Records a payment directly against a saved invoice by id, and persists immediately. */
    fun addPaymentToInvoice(invoiceId: Long, amount: BigDecimal, method: String? = null, note: String? = null) {
        viewModelScope.launch {
            invoiceRepository.getInvoiceById(invoiceId)?.let { invoice ->
                val payment = Payment(
                    invoiceId = invoiceId,
                    amount = amount,
                    date = System.currentTimeMillis(),
                    method = method,
                    note = note
                )
                invoiceRepository.updateInvoice(invoice.copy(payments = invoice.payments + payment))
            }
        }
    }

    // ---- Save / delete --------------------------------------------------

    fun saveInvoice(onSaved: (Long) -> Unit = {}) {
        val invoice = _detailState.value.invoice ?: return

        viewModelScope.launch {
            _detailState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val invoiceId = if (invoice.id == 0L) {
                    invoiceRepository.insertInvoice(invoice)
                } else {
                    invoiceRepository.updateInvoice(invoice)
                    invoice.id
                }
                _detailState.update { it.copy(isSaving = false) }
                onSaved(invoiceId)
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "Failed to save invoice")
                }
            }
        }
    }

    fun deleteInvoice(id: Long, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                invoiceRepository.deleteInvoice(id)
                onDeleted()
            } catch (e: Exception) {
                _detailState.update { it.copy(errorMessage = e.message ?: "Failed to delete invoice") }
            }
        }
    }

    fun deleteInvoiceFromList(id: Long) {
        viewModelScope.launch { invoiceRepository.deleteInvoice(id) }
    }

    // ---- Quick status actions from the list screen -----------------------

    fun markAsCancelled(id: Long) = updateStatusById(id, InvoiceStatus.CANCELLED)

    private fun updateStatusById(id: Long, status: InvoiceStatus) {
        viewModelScope.launch {
            invoiceRepository.getInvoiceById(id)?.let {
                invoiceRepository.updateInvoice(it.copy(status = status))
            }
        }
    }

    fun clearError() {
        _detailState.update { it.copy(errorMessage = null) }
    }

    fun resetDetailState() {
        _detailState.value = InvoiceDetailState()
    }

    companion object {
        private const val THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000
    }
}