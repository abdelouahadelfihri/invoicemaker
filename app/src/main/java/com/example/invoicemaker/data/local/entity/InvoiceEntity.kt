package com.example.invoicemaker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = EstimateEntity::class,
            parentColumns = ["id"],
            childColumns = ["estimateId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("clientId"), Index("estimateId"), Index(value = ["invoiceNumber"], unique = true)]
)
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,         // e.g. "INV-2026-0001"
    val clientId: Long,
    val estimateId: Long? = null,      // set if converted from an estimate
    val issueDate: Long,
    val dueDate: Long? = null,
    val status: String = "DRAFT",      // DRAFT, SENT, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED
    val subtotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val discountIsPercentage: Boolean = false,
    val taxAmount: Double = 0.0,
    val total: Double = 0.0,
    val amountPaid: Double = 0.0,
    val currency: String = "USD",
    val notes: String? = null,
    val terms: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)