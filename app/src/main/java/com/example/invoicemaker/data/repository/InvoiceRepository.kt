package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.local.dao.InvoiceDao
import com.example.invoicemaker.data.local.entity.InvoiceEntity
import com.example.invoicemaker.data.InvoiceStatus
import kotlinx.coroutines.flow.Flow

class InvoiceRepository(
    private val dao: InvoiceDao
) {

    // --- Powers the pre-filled "INV00002" on InvoiceInfoScreen ---
    suspend fun generateNextInvoiceNumber(): String {
        val last = dao.getLastInvoiceNumber() ?: return "INV00001"

        val digits = last.filter { it.isDigit() }
        val nextNumber = (digits.toIntOrNull() ?: 0) + 1

        return "INV" + nextNumber.toString().padStart(5, '0')
    }

    // --- Existing repository methods ---

    fun observeAll(): Flow<List<InvoiceEntity>> = dao.observeAll()

    suspend fun getById(id: Long): InvoiceEntity? = dao.getById(id)

    suspend fun save(invoice: InvoiceEntity): Long =
        if (invoice.id == 0L) dao.insert(invoice) else {
            dao.update(invoice)
            invoice.id
        }

    // --- New: needed by InvoicesViewModel.deleteInvoice() / markAsPaid() ---

    suspend fun deleteInvoice(id: Long) {
        dao.deleteById(id)
    }

    suspend fun updateStatus(id: Long, status: InvoiceStatus) {
        dao.getById(id)?.let { invoice ->
            dao.update(invoice.copy(status = status))
        }
    }
}