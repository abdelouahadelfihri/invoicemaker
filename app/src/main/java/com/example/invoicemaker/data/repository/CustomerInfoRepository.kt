package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.local.dao.BusinessInfoDao
import com.example.invoicemaker.data.local.entity.BusinessInfoEntity
import com.example.invoicemaker.data.BusinessInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Single source of truth for Business Info.
 *
 * - BusinessInfoScreen (via BusinessInfoViewModel) reads/writes through here.
 * - InvoiceAddEditViewModel and EstimateAddEditViewModel collect [observe]
 *   so the invoice/estimate preview always reflects the current business
 *   profile, since both features share this one repository instance.
 *
 * Provide as a singleton (Hilt @Singleton or manual DI) so every screen
 * observes the same Flow.
 */
class CustomerInfoRepository(
    private val dao: BusinessInfoDao
) {

    // Emits BusinessInfo.Empty until the user saves something for the
    // first time — callers never have to deal with null.
    fun observe(): Flow<BusinessInfo> =
        dao.observe().map { entity -> entity?.toDomain() ?: BusinessInfo.Empty }

    suspend fun getOnce(): BusinessInfo =
        dao.getOnce()?.toDomain() ?: BusinessInfo.Empty

    suspend fun save(businessInfo: BusinessInfo) {
        dao.upsert(businessInfo.toEntity())
    }

    suspend fun clear() {
        dao.clear()
    }

    private fun BusinessInfoEntity.toDomain() = BusinessInfo(
        businessName = businessName,
        email = email,
        phone = phone,
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        website = website,
        taxName = taxName,
        taxId = taxId,
        logoPath = logoPath
    )

    private fun BusinessInfo.toEntity() = BusinessInfoEntity(
        businessName = businessName,
        email = email,
        phone = phone,
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        website = website,
        taxName = taxName,
        taxId = taxId,
        logoPath = logoPath
    )
}