package com.example.invoicemaker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "estimates",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.RESTRICT // don't silently orphan financial docs
        )
    ],
    indices = [Index("clientId"), Index(value = ["estimateNumber"], unique = true)]
)
data class EstimateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val estimateNumber: String,        // e.g. "EST-2026-0001"
    val clientId: Long,
    val issueDate: Long,
    val expiryDate: Long? = null,
    val status: String = "DRAFT",      // DRAFT, SENT, ACCEPTED, DECLINED, EXPIRED, CONVERTED
    val subtotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val discountIsPercentage: Boolean = false,
    val taxAmount: Double = 0.0,
    val total: Double = 0.0,
    val currency: String = "USD",
    val notes: String? = null,
    val terms: String? = null,
    val convertedInvoiceId: Long? = null, // set once turned into an invoice
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)