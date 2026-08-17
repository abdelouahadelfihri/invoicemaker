package com.example.invoicemaker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.invoicemaker.data.local.entity.BusinessInfoEntity
import com.example.invoicemaker.data.local.entity.BusinessInfoEntity.Companion.SINGLETON_ID
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessInfoDao {

    // Emits null until the user has saved Business Info at least once.
    // Repository can map that null -> a default/empty BusinessInfo.
    @Query("SELECT * FROM business_info WHERE id = :id LIMIT 1")
    fun observe(id: Int = SINGLETON_ID): Flow<BusinessInfoEntity?>

    @Query("SELECT * FROM business_info WHERE id = :id LIMIT 1")
    suspend fun getOnce(id: Int = SINGLETON_ID): BusinessInfoEntity?

    // Insert-or-replace on the fixed id — this is your single "save" call
    // from BusinessInfoScreen's validate/check button.
    @Upsert
    suspend fun upsert(businessInfo: BusinessInfoEntity)

    @Query("DELETE FROM business_info WHERE id = :id")
    suspend fun clear(id: Int = SINGLETON_ID)
}