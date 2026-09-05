package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.local.dao.ClientDao
import com.example.invoicemaker.data.local.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

class ClientRepository(
    private val dao: ClientDao
) {
    fun observeAll(): Flow<List<ClientEntity>> = dao.observeAll()
    suspend fun getById(id: Long): ClientEntity? = dao.getById(id)
    fun observeSearch(query: String): Flow<List<ClientEntity>> = dao.observeSearch(query)
    suspend fun getCount(): Int = dao.getCount()

    suspend fun save(client: ClientEntity): Long =
        if (client.id == 0L) dao.insert(client) else {
            dao.update(client)
            client.id
        }

    suspend fun delete(client: ClientEntity) = dao.delete(client)
    suspend fun deleteById(id: Long) {
        getById(id)?.let { dao.delete(it) }
    }
}