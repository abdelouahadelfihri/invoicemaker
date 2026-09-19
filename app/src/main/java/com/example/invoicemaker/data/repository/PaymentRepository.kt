package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.local.dao.PaymentDao
import com.example.invoicemaker.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

class PaymentRepository(
    private val dao: PaymentDao
) {
    // --- Needed by InvoicesViewModel to build UI models with amountPaid/amountDue ---
    fun observeAll(): Flow<List<PaymentEntity>> = dao.observeAll()

    fun observeForInvoice(invoiceId: Long): Flow<List<PaymentEntity>> =
        dao.observeForInvoice(invoiceId)

    suspend fun addPayment(payment: PaymentEntity): Long = dao.insert(payment)

    suspend fun deletePayment(payment: PaymentEntity) = dao.delete(payment)

    suspend fun deleteAllForInvoice(invoiceId: Long) {
        dao.deleteAllForInvoice(invoiceId)
    }
}