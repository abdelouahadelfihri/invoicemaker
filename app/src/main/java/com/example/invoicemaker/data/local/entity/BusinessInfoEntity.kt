package com.example.invoicemaker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Business Info is a single-row table — there is only ever one business
 * profile per app install, so the primary key is fixed at [SINGLETON_ID].
 * Always read/write using that id (see BusinessInfoDao).
 */
@Entity(tableName = "business_info")
data class BusinessInfoEntity(
    @PrimaryKey
    val id: Int = SINGLETON_ID,
    val businessName: String = "",
    val email: String = "",
    val phone: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val website: String = "",
    val taxName: String = "",
    val taxId: String = "",
    // Local file path (or content URI string) to the saved logo image.
    // Store a copied file in app-internal storage rather than the raw
    // picker/camera URI, since gallery/camera URIs aren't guaranteed
    // to stay valid across app restarts.
    val logoPath: String? = null
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}