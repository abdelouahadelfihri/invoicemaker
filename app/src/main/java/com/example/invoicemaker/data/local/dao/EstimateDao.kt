package com.example.invoicemaker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.invoicemaker.data.local.entity.EstimateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimateDao {

    @Query("SELECT * FROM estimates ORDER BY id DESC")
    fun observeAll(): Flow<List<EstimateEntity>>

    @Query("SELECT * FROM estimates WHERE id = :id")
    suspend fun getById(id: Long): EstimateEntity?

    @Query("SELECT estimateNumber FROM estimates ORDER BY id DESC LIMIT 1")
    suspend fun getLastEstimateNumber(): String?

    @Insert
    suspend fun insert(estimate: EstimateEntity): Long

    @Update
    suspend fun update(estimate: EstimateEntity)

    @Query("DELETE FROM estimates WHERE id = :id")
    suspend fun deleteById(id: Long)
}