package com.example.invoicemaker.data.local.dao

import androidx.room.*
import com.yourpackage.invoicemaker.data.local.entity.InvoiceEntity
import com.yourpackage.invoicemaker.data.local.entity.InvoiceLineEntity
import com.yourpackage.invoicemaker.data.local.entity.PaymentEntity
import com.yourpackage.invoicemaker.data.local.relation.InvoiceWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {

    @Transaction
    @Query("SELECT * FROM invoices ORDER BY issueDate DESC")
    fun getAllInvoicesWithDetails(): Flow<List<InvoiceWithDetails>>

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    fun getInvoiceWithDetails(invoiceId: Long): Flow<InvoiceWithDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLines(lines: List<InvoiceLineEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)

    @Query("UPDATE invoices SET status = :status WHERE id = :invoiceId")
    suspend fun updateStatus(invoiceId: Long, status: String)

    @Delete
    suspend fun deleteInvoice(invoice: InvoiceEntity)

    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoiceById(invoiceId: Long)

    @Query("DELETE FROM invoice_lines WHERE invoiceId = :invoiceId")
    suspend fun deleteLinesForInvoice(invoiceId: Long)
}