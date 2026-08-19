package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.local.dao.InvoiceDao
import com.example.invoicemaker.data.local.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow

class InvoiceRepository(
    private val dao: InvoiceDao
) {

    // --- New: powers the pre-filled "INV00002" on InvoiceInfoScreen ---
    // Reads the last saved invoice number ("INV00001"), strips the
    // "INV" prefix, increments, and re-pads to 5 digits.
    // First-ever invoice (no rows yet) starts at "INV00001".
    suspend fun generateNextInvoiceNumber(): String {
        val last = dao.getLastInvoiceNumber() ?: return "INV00001"

        val digits = last.filter { it.isDigit() }
        val nextNumber = (digits.toIntOrNull() ?: 0) + 1

        return "INV" + nextNumber.toString().padStart(5, '0')
    }

    // --- Your existing repository methods, kept here for context ---

    fun observeAll(): Flow<List<InvoiceEntity>> = dao.observeAll()

    suspend fun getById(id: Long): InvoiceEntity? = dao.getById(id)

    suspend fun save(invoice: InvoiceEntity): Long =
        if (invoice.id == 0L) dao.insert(invoice) else {
            dao.update(invoice)
            invoice.id
        }
}