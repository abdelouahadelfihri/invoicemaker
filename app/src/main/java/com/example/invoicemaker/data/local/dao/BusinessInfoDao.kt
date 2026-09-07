// com.example.invoicemaker.data.local.dao.BusinessInfoDao.kt
package com.example.invoicemaker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.invoicemaker.data.local.entity.BusinessInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessInfoDao {
    @Query("SELECT * FROM business_info WHERE id = 1")
    fun observe(): Flow<BusinessInfoEntity?>

    @Query("SELECT * FROM business_info WHERE id = 1")
    suspend fun get(): BusinessInfoEntity?

    @Upsert
    suspend fun save(businessInfo: BusinessInfoEntity)
}