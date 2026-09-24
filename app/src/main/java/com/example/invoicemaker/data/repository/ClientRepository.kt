package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.local.dao.ClientDao
import com.example.invoicemaker.data.local.entity.Client
import kotlinx.coroutines.flow.Flow

class ClientRepository(
    private val clientDao: ClientDao
) {

    fun observeAllClients(): Flow<List<Client>> =
        clientDao.observeAll()

    fun searchClients(query: String): Flow<List<Client>> =
        clientDao.searchClients(query)

    suspend fun getClientById(id: Long): Client? =
        clientDao.getClientById(id)

    // CHANGED — now returns the new row id
    suspend fun insertClient(client: Client): Long =
        clientDao.insertClient(client)               // must return Long, not Unit

    suspend fun updateClient(client: Client) {
        clientDao.updateClient(client)
    }

    suspend fun deleteClient(id: Long) {
        clientDao.deleteClient(id)
    }
}