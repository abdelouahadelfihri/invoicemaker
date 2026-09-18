package com.example.invoicemaker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.invoicemaker.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceItemDao {

    // --- Used by InvoicesViewModel to build InvoiceUiModel for every invoice at once ---
    @Query("SELECT * FROM invoice_items ORDER BY invoiceId, sortOrder")
    fun observeAll(): Flow<List<InvoiceItemEntity>>

    // --- Per-invoice queries, useful for an invoice detail screen ---
    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId ORDER BY sortOrder")
    fun observeForInvoice(invoiceId: Long): Flow<List<InvoiceItemEntity>>

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId ORDER BY sortOrder")
    suspend fun getForInvoice(invoiceId: Long): List<InvoiceItemEntity>

    @Query("SELECT * FROM invoice_items WHERE id = :id")
    suspend fun getById(id: Long): InvoiceItemEntity?

    @Insert
    suspend fun insert(item: InvoiceItemEntity): Long

    @Insert
    suspend fun insertAll(items: List<InvoiceItemEntity>)

    @Update
    suspend fun update(item: InvoiceItemEntity)

    @Delete
    suspend fun delete(item: InvoiceItemEntity)

    @Query("DELETE FROM invoice_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM invoice_items WHERE invoiceId = :invoiceId")
    suspend fun deleteAllForInvoice(invoiceId: Long)
}