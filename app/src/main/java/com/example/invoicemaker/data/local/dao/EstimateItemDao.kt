package com.example.invoicemaker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.invoicemaker.data.local.entity.EstimateItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimateItemDao {

    @Query("SELECT * FROM estimate_items ORDER BY estimateId, sortOrder")
    fun observeAll(): Flow<List<EstimateItemEntity>>

    @Query("SELECT * FROM estimate_items WHERE estimateId = :estimateId ORDER BY sortOrder")
    fun observeForEstimate(estimateId: Long): Flow<List<EstimateItemEntity>>

    @Query("SELECT * FROM estimate_items WHERE estimateId = :estimateId ORDER BY sortOrder")
    suspend fun getForEstimate(estimateId: Long): List<EstimateItemEntity>

    @Insert
    suspend fun insert(item: EstimateItemEntity): Long

    @Insert
    suspend fun insertAll(items: List<EstimateItemEntity>)

    @Update
    suspend fun update(item: EstimateItemEntity)

    @Delete
    suspend fun delete(item: EstimateItemEntity)

    @Query("DELETE FROM estimate_items WHERE estimateId = :estimateId")
    suspend fun deleteAllForEstimate(estimateId: Long)
}