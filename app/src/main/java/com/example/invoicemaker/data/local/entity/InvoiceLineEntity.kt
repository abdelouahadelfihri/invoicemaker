package com.example.invoicemaker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "invoice_lines",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE     // deleting an invoice deletes its lines
        )
    ],
    indices = [Index("invoiceId")]
)
data class InvoiceLineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val itemId: Long?,
    val description: String,
    val quantity: BigDecimal,
    val unit: String,                 // store ItemUnit as string
    val unitPrice: BigDecimal,
    val taxRate: BigDecimal,
    val discount: BigDecimal = BigDecimal.ZERO,
    val sortOrder: Int = 0
)