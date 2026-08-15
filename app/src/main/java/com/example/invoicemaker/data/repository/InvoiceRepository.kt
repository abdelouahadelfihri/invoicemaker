package com.example.invoicemaker.data.repository

import com.yourpackage.invoicemaker.data.*
import com.yourpackage.invoicemaker.data.local.dao.InvoiceDao
import com.yourpackage.invoicemaker.data.local.entity.InvoiceEntity
import com.yourpackage.invoicemaker.data.local.entity.InvoiceLineEntity
import com.yourpackage.invoicemaker.data.local.entity.PaymentEntity
import com.yourpackage.invoicemaker.data.local.relation.InvoiceWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InvoiceRepository(private val invoiceDao: InvoiceDao) {

    fun getAllInvoices(): Flow<List<Invoice>> =
        invoiceDao.getAllInvoicesWithDetails().map { list -> list.map { it.toDomain() } }

    fun getInvoice(id: Long): Flow<Invoice?> =
        invoiceDao.getInvoiceWithDetails(id).map { it?.toDomain() }

    suspend fun saveInvoice(invoice: Invoice) {
        val invoiceId = invoiceDao.insertInvoice(invoice.toEntity())
        invoiceDao.deleteLinesForInvoice(invoiceId) // simplest approach: replace all lines
        invoiceDao.insertLines(invoice.lines.map { it.toEntity(invoiceId) })
    }

    suspend fun deleteInvoice(id: Long) = invoiceDao.deleteInvoiceById(id)

    suspend fun updateStatus(id: Long, status: InvoiceStatus) =
        invoiceDao.updateStatus(id, status.name)

    suspend fun addPayment(payment: Payment) =
        invoiceDao.insertPayment(payment.toEntity())
}

// ---- Mappers ----

private fun InvoiceWithDetails.toDomain(): Invoice = Invoice(
    id = invoice.id,
    invoiceNumber = invoice.invoiceNumber,
    clientId = invoice.clientId,
    lines = lines.map { it.toDomain() },
    payments = payments.map { it.toDomain() },
    status = InvoiceStatus.valueOf(invoice.status),
    issueDate = invoice.issueDate,
    dueDate = invoice.dueDate,
    notes = invoice.notes,
    sourceEstimateId = invoice.sourceEstimateId,
    createdAt = invoice.createdAt
)

private fun InvoiceLineEntity.toDomain(): InvoiceLine = InvoiceLine(
    id = id, invoiceId = invoiceId, itemId = itemId, description = description,
    quantity = quantity, unit = ItemUnit.valueOf(unit), unitPrice = unitPrice,
    taxRate = taxRate, discount = discount, sortOrder = sortOrder
)

private fun PaymentEntity.toDomain(): Payment = Payment(
    id = id, invoiceId = invoiceId, amount = amount, date = date, method = method, note = note
)

private fun Invoice.toEntity(): InvoiceEntity = InvoiceEntity(
    id = id, invoiceNumber = invoiceNumber, clientId = clientId, status = status.name,
    issueDate = issueDate, dueDate = dueDate, notes = notes,
    sourceEstimateId = sourceEstimateId, createdAt = createdAt
)

private fun InvoiceLine.toEntity(invoiceId: Long): InvoiceLineEntity = InvoiceLineEntity(
    id = id, invoiceId = invoiceId, itemId = itemId, description = description,
    quantity = quantity, unit = unit.name, unitPrice = unitPrice, taxRate = taxRate,
    discount = discount, sortOrder = sortOrder
)

private fun Payment.toEntity(): PaymentEntity = PaymentEntity(
    id = id, invoiceId = invoiceId, amount = amount, date = date, method = method, note = note
)