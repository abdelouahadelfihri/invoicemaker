package com.example.invoicemaker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.invoicemaker.data.local.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

// ---------------------------------------------------------------------------
// Assumes a "clients" table with at least: id, name, email, phone, address.
// Rename the columns in the @Query strings below if your ClientEntity
// uses different field names.
// ---------------------------------------------------------------------------

@Dao
interface ClientDao {

    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun observeAll(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getById(id: Long): ClientEntity?

    @Query(
        """
        SELECT * FROM clients
        WHERE name LIKE '%' || :query || '%'
           OR email LIKE '%' || :query || '%'
        ORDER BY name ASC
        """
    )
    fun observeSearch(query: String): Flow<List<ClientEntity>>

    @Query("SELECT COUNT(*) FROM clients")
    suspend fun getCount(): Int

    @Insert
    suspend fun insert(client: ClientEntity): Long

    @Update
    suspend fun update(client: ClientEntity)

    @Delete
    suspend fun delete(client: ClientEntity)
}