package com.example.invoicemaker.data.local.dao

import androidx.room.*
import com.example.invoicemaker.data.local.entity.EstimateEntity
import com.example.invoicemaker.data.local.entity.EstimateItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimateItemDao {
    @Query("SELECT * FROM estimate_items WHERE estimateId = :estimateId ORDER BY sortOrder ASC")
    suspend fun getForEstimate(estimateId: Long): List<EstimateItemEntity>

    @Insert
    suspend fun insertAll(items: List<EstimateItemEntity>)

    @Query("DELETE FROM estimate_items WHERE estimateId = :estimateId")
    suspend fun deleteForEstimate(estimateId: Long)
}