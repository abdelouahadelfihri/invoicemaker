package com.example.invoicemaker.ui.screens.invoices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.local.InvoiceDatabase
import com.example.invoicemaker.data.local.entity.InvoiceEntity
import com.example.invoicemaker.data.local.entity.InvoiceItemEntity
import com.example.invoicemaker.data.local.entity.InvoiceStatus
import com.example.invoicemaker.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
class InvoicesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = InvoiceDatabase.getInstance(application) // ASSUMPTION: adjust to your actual DB accessor
    private val invoiceDao = db.invoiceDao()
    private val clientDao = db.clientDao()
    private val invoiceItemDao = db.invoiceItemDao() // ASSUMPTION: DAO name
    private val paymentDao = db.paymentDao()         // ASSUMPTION: DAO name

    /**
     * Combines invoices with their client name, items, and payments,
     * then maps each to a fully computed InvoiceUiModel.
     *
     * ASSUMPTION: clientDao.observeAll() returns Flow<List<ClientEntity>>
     * ASSUMPTION: invoiceItemDao.observeAll() returns Flow<List<InvoiceItemEntity>>
     * ASSUMPTION: paymentDao.observeAll() returns Flow<List<PaymentEntity>>
     * If instead you have per-invoice queries (observeItemsForInvoice(id), etc.),
     * tell me and I'll rewrite this using flatMapLatest per invoice instead.
     */
    val invoices: Flow<List<InvoiceUiModel>> = combine(
        invoiceDao.observeAll(),
        clientDao.observeAll(),
        invoiceItemDao.observeAll(),
        paymentDao.observeAll()
    ) { invoiceList, clients, allItems, allPayments ->
        val clientNameById = clients.associateBy({ it.id }, { it.name }) // ASSUMPTION: ClientEntity has id, name
        val itemsByInvoiceId = allItems.groupBy { it.invoiceId }         // ASSUMPTION: InvoiceItemEntity has invoiceId
        val paymentsByInvoiceId = allPayments.groupBy { it.invoiceId }   // ASSUMPTION: PaymentEntity has invoiceId

        invoiceList.map { invoice ->
            buildInvoiceUiModel(
                invoice = invoice,
                clientName = clientNameById[invoice.clientId] ?: "Unknown Client",
                items = itemsByInvoiceId[invoice.id].orEmpty(),
                payments = paymentsByInvoiceId[invoice.id].orEmpty()
            )
        }
    }

    fun deleteInvoice(id: Long) {
        viewModelScope.launch {
            invoiceDao.deleteById(id)
        }
    }

    fun markAsPaid(id: Long) {
        viewModelScope.launch {
            val invoice = invoiceDao.getById(id) ?: return@launch
            invoiceDao.update(invoice.copy(status = InvoiceStatus.PAID.name))
            // NOTE: InvoiceEntity is declared as `class`, not `data class`,
            // so .copy() won't work until you change it to `data class InvoiceEntity(...)`
        }
    }

    suspend fun generateNextInvoiceNumber(): String {
        val last = invoiceDao.getLastInvoiceNumber()
        val nextNumber = last
            ?.substringAfterLast("-")
            ?.toIntOrNull()
            ?.plus(1) ?: 1
        return "INV-%04d".format(nextNumber)
    }
}

// --- Mapper ---

private fun buildInvoiceUiModel(
    invoice: InvoiceEntity,
    clientName: String,
    items: List<InvoiceItemEntity>,
    payments: List<PaymentEntity>
): InvoiceUiModel {
    // lineTotal is treated as the net amount (quantity * unitPrice) before tax.
    // Tax per line is derived from taxRate (a percentage, e.g. 20.0 for 20%).
    val subtotal = items.fold(BigDecimal.ZERO) { acc, item ->
        acc + BigDecimal.valueOf(item.lineTotal)
    }
    val totalTax = items.fold(BigDecimal.ZERO) { acc, item ->
        val lineTax = BigDecimal.valueOf(item.lineTotal)
            .multiply(BigDecimal.valueOf(item.taxRate))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        acc + lineTax
    }
    val total = subtotal + totalTax

    val amountPaid = payments.fold(BigDecimal.ZERO) { acc, payment ->
        acc + BigDecimal.valueOf(payment.amount) // ASSUMPTION: PaymentEntity has a Double `amount` field — confirm when you share it
    }
    val amountDue = (total - amountPaid).coerceAtLeast(BigDecimal.ZERO)

    val storedStatus = InvoiceStatus.entries.find { it.name == invoice.status } ?: InvoiceStatus.UNPAID

    val computedStatus = when {
        storedStatus == InvoiceStatus.CANCELLED -> InvoiceStatus.CANCELLED
        amountDue <= BigDecimal.ZERO -> InvoiceStatus.PAID
        amountPaid > BigDecimal.ZERO -> InvoiceStatus.PARTIALLY_PAID
        invoice.dueDate < System.currentTimeMillis() -> InvoiceStatus.OVERDUE
        else -> InvoiceStatus.UNPAID
    }

    return InvoiceUiModel(
        id = invoice.id,
        invoiceNumber = invoice.invoiceNumber,
        clientName = clientName,
        status = computedStatus,
        issueDate = invoice.issueDate,
        dueDate = invoice.dueDate,
        itemCount = items.size,
        paymentCount = payments.size,
        subtotal = subtotal,
        totalTax = totalTax,
        total = total,
        amountPaid = amountPaid,
        amountDue = amountDue
    )
}