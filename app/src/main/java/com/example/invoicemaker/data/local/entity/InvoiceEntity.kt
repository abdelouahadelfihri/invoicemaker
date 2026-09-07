// data.local.entity.InvoiceEntity.kt
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
    indices = [Index("clientId"), Index(value = ["invoiceNumber"], unique = true)]
)
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val clientId: Long,
    val issueDate: Long,
    val dueDate: Long,
    val status: String = "UNPAID",
    val notes: String? = null,
    val sourceEstimateId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)