package com.example.invoicemaker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for the single Business Info profile.
 * Always stored/read with id = 0 (single-row table).
 */
@Entity(tableName = "business_info")
data class BusinessInfoEntity(
    @PrimaryKey
    val id: Int = 0,
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