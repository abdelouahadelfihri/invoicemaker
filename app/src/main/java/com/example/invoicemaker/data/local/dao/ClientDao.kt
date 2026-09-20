package com.example.invoicemaker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.invoicemaker.data.local.entity.Client

@Dao
interface ClientDao {

    @Insert
    suspend fun insertClient(client: Client)

    @Update
    suspend fun updateClient(client: Client)

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun deleteClient(id: Long)

    @Query("""
    SELECT * FROM clients 
    WHERE name LIKE '%' || :query || '%' 
       OR companyName LIKE '%' || :query || '%' 
       OR phone LIKE '%' || :query || '%' 
       OR email LIKE '%' || :query || '%'
       OR address LIKE '%' || :query || '%'
       OR city LIKE '%' || :query || '%'
       OR taxId LIKE '%' || :query || '%'
       OR notes LIKE '%' || :query || '%'
    """)
    fun searchClients(query: String): Flow<List<Client>>

    @Query("SELECT * FROM clients")
    fun observeAll(): Flow<List<Client>>
}