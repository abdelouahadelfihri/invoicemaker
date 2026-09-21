package com.example.invoicemaker.ui.screens.invoices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.local.InvoiceDatabase
import com.example.invoicemaker.data.local.entity.InvoiceEntity
import com.example.invoicemaker.data.local.entity.InvoiceItemEntity
import com.example.invoicemaker.data.local.entity.InvoiceStatus
import com.example.invoicemaker.data.local.entity.PaymentEntity
import com.example.invoicemaker.data.repository.InvoiceItemRepository
import com.example.invoicemaker.data.repository.InvoiceRepository
import com.example.invoicemaker.data.repository.PaymentRepository
import com.example.invoicemaker.data.repository.ClientRepository // ASSUMPTION: exists, mirrors other repositories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class InvoicesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = InvoiceDatabase.getInstance(application)

    private val invoiceRepository = InvoiceRepository(dao = db.invoiceDao())
    private val invoiceItemRepository = InvoiceItemRepository(dao = db.invoiceItemDao())
    private val paymentRepository = PaymentRepository(dao = db.paymentDao())
    private val clientRepository = ClientRepository(clientDao = db.clientDao()) // ASSUMPTION: constructor shape

    val invoices: Flow<List<InvoiceUiModel>> = combine(
        invoiceRepository.observeAll(),
        clientRepository.observeAllClients(), // ASSUMPTION: method exists
        invoiceItemRepository.observeAll(),
        paymentRepository.observeAll()
    ) { invoiceList, clients, allItems, allPayments ->
        val clientNameById = clients.associateBy({ it.id }, { it.name }) // ASSUMPTION: ClientEntity has id, name
        val itemsByInvoiceId = allItems.groupBy { it.invoiceId }
        val paymentsByInvoiceId = allPayments.groupBy { it.invoiceId }

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
        viewModelScope.launch { invoiceRepository.deleteInvoice(id) }
    }

    fun markAsPaid(id: Long) {
        viewModelScope.launch { invoiceRepository.updateStatus(id, InvoiceStatus.PAID) }
    }

    suspend fun generateNextInvoiceNumber(): String = invoiceRepository.generateNextInvoiceNumber()
}

// --- Mapper (unchanged) ---

private fun buildInvoiceUiModel(
    invoice: InvoiceEntity,
    clientName: String,
    items: List<InvoiceItemEntity>,
    payments: List<PaymentEntity>
): InvoiceUiModel {
    val subtotal = items.fold(BigDecimal.ZERO) { acc, item -> acc + BigDecimal.valueOf(item.lineTotal) }
    val totalTax = items.fold(BigDecimal.ZERO) { acc, item ->
        val lineTax = BigDecimal.valueOf(item.lineTotal)
            .multiply(BigDecimal.valueOf(item.taxRate))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        acc + lineTax
    }
    val total = subtotal + totalTax

    val amountPaid = payments.fold(BigDecimal.ZERO) { acc, payment -> acc + BigDecimal.valueOf(payment.amount) }
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