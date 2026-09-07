package com.example.invoicemaker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_info")
data class BusinessInfoEntity(
    @PrimaryKey val id: Long = 1L, // fixed id — enforces single row
    val businessName: String = "",
    val email: String = "",
    val phone: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val website: String = "",
    val taxName: String = "",
    val taxId: String = "",
    val logoPath: String? = null
)