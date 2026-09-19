package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.local.dao.InvoiceItemDao
import com.example.invoicemaker.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

class InvoiceItemRepository(
    private val dao: InvoiceItemDao
) {
    // --- Needed by InvoicesViewModel to build UI models with item totals ---
    fun observeAll(): Flow<List<InvoiceItemEntity>> = dao.observeAll()

    fun observeForInvoice(invoiceId: Long): Flow<List<InvoiceItemEntity>> =
        dao.observeForInvoice(invoiceId)

    suspend fun getForInvoice(invoiceId: Long): List<InvoiceItemEntity> =
        dao.getForInvoice(invoiceId)

    suspend fun saveItemsFor(invoiceId: Long, items: List<InvoiceItemEntity>) {
        dao.deleteAllForInvoice(invoiceId)
        dao.insertAll(items.map { it.copy(invoiceId = invoiceId) })
    }

    suspend fun deleteAllForInvoice(invoiceId: Long) {
        dao.deleteAllForInvoice(invoiceId)
    }
}