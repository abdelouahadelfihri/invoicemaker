package com.example.invoicemaker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.invoicemaker.data.local.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    // --- Add this query alongside whatever you already have ---
    // Gets the most recently created invoice's number so the repository
    // can compute the next one. Ordering by id (autoGenerate) is safer
    // than parsing/sorting the string invoiceNumber itself.
    @Query("SELECT invoiceNumber FROM invoices ORDER BY id DESC LIMIT 1")
    suspend fun getLastInvoiceNumber(): String?

    // --- Your existing CRUD, kept here for context ---

    @Query("SELECT * FROM invoices ORDER BY id DESC")
    fun observeAll(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getById(id: Long): InvoiceEntity?

    @Insert
    suspend fun insert(invoice: InvoiceEntity): Long

    @Update
    suspend fun update(invoice: InvoiceEntity)
}