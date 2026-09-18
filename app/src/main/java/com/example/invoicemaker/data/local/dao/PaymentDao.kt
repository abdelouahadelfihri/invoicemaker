package com.example.invoicemaker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.invoicemaker.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    // --- Used by InvoicesViewModel to build InvoiceUiModel for every invoice at once ---
    @Query("SELECT * FROM payments ORDER BY invoiceId, date DESC")
    fun observeAll(): Flow<List<PaymentEntity>>

    // --- Per-invoice queries, useful for an invoice detail screen ---
    @Query("SELECT * FROM payments WHERE invoiceId = :invoiceId ORDER BY date DESC")
    fun observeForInvoice(invoiceId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE invoiceId = :invoiceId ORDER BY date DESC")
    suspend fun getForInvoice(invoiceId: Long): List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE id = :id")
    suspend fun getById(id: Long): PaymentEntity?

    @Insert
    suspend fun insert(payment: PaymentEntity): Long

    @Update
    suspend fun update(payment: PaymentEntity)

    @Delete
    suspend fun delete(payment: PaymentEntity)

    @Query("DELETE FROM payments WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM payments WHERE invoiceId = :invoiceId")
    suspend fun deleteAllForInvoice(invoiceId: Long)
}