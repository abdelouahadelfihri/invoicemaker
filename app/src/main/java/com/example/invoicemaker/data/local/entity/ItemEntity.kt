package com.example.invoicemaker.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    indices = [Index(value = ["sku"], unique = true)]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String? = null,
    val sku: String? = null,
    val unit: String = "pcs",          // pcs, hr, kg, day, etc.
    val unitPrice: Double = 0.0,
    val taxRate: Double = 0.0,         // percentage, e.g. 20.0 = 20%
    val isActive: Boolean = true,      // soft-hide from picker without deleting
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)