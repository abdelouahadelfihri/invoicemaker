package com.example.invoicemaker.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.invoicemaker.data.local.entity.Client

@Dao
interface ClientDao {

    @Insert
    fun insertClient(client: Client)

    @Query("SELECT * FROM products WHERE productName = :name")
    fun findClient(name: String): List<Client>

    @Query("DELETE FROM products WHERE productName = :name")
    fun deleteClient(name: String)

    @Query("SELECT * FROM products")
    fun getAllClients(): LiveData<List<Client>>

}