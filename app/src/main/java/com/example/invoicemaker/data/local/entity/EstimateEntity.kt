package com.example.invoicemaker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "estimates",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("clientId")]
)
data class EstimateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val estimateNumber: String,
    val clientId: Long,
    val status: String = "DRAFT",
    val issueDate: Long,
    val expiryDate: Long,
    val notes: String? = null,
    val termsAndConditions: String? = null,
    val convertedInvoiceId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)