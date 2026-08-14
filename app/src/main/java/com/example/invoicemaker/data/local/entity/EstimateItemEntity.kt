package com.example.invoicemaker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "estimate_items",
    foreignKeys = [
        ForeignKey(
            entity = EstimateEntity::class,
            parentColumns = ["id"],
            childColumns = ["estimateId"],
            onDelete = ForeignKey.CASCADE   // delete lines when estimate is deleted
        ),
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.SET_NULL  // keep the line even if catalog item is removed
        )
    ],
    indices = [Index("estimateId"), Index("itemId")]
)
data class EstimateItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val estimateId: Long,
    val itemId: Long? = null,
    val description: String,
    val quantity: Double = 1.0,
    val unitPrice: Double = 0.0,
    val taxRate: Double = 0.0,
    val lineTotal: Double = 0.0,       // quantity * unitPrice, tax applied at estimate level or here
    val sortOrder: Int = 0
)