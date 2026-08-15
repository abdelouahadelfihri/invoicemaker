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
        )
    ],
    indices = [Index("clientId")]
)
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val clientId: Long,
    val status: String,              // store enum as string, e.g. InvoiceStatus.UNPAID.name
    val issueDate: Long,
    val dueDate: Long,
    val notes: String? = null,
    val sourceEstimateId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)