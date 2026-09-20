package com.example.invoicemaker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import com.example.invoicemaker.data.local.entity.BusinessInfo

@Dao
interface BusinessInfoDao {

    @Insert
    suspend fun insertBusinessInfo(client: BusinessInfo)

    @Update
    suspend fun updateBusinessInfo(client: BusinessInfo)

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun deleteBusinessInfo(id: Long)

}