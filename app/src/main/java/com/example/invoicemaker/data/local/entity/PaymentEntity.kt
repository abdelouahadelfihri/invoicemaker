package com.example.invoicemaker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("invoiceId")]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceId: Long,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val method: String = "CASH",       // CASH, BANK_TRANSFER, CARD, CHECK, OTHER
    val reference: String? = null,     // transaction id / check number
    val notes: String? = null
)