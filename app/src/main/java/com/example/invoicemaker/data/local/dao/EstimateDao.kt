package com.example.invoicemaker.data.local.dao

import androidx.room.*
import com.example.invoicemaker.data.local.entity.EstimateEntity
import com.example.invoicemaker.data.local.entity.EstimateItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimateDao {
    @Query("SELECT * FROM estimates ORDER BY issueDate DESC")
    fun observeAll(): Flow<List<EstimateEntity>>

    @Query("SELECT * FROM estimates WHERE id = :id")
    suspend fun getById(id: Long): EstimateEntity?

    @Query("SELECT COUNT(*) FROM estimates")
    suspend fun getCount(): Int

    @Insert
    suspend fun insert(estimate: EstimateEntity): Long

    @Update
    suspend fun update(estimate: EstimateEntity)

    @Delete
    suspend fun delete(estimate: EstimateEntity)
}