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

    suspend fun insertClient(client: Client) {
        clientDao.insertClient(client)
    }

    suspend fun updateClient(client: Client) {
        clientDao.updateClient(client)
    }

    suspend fun deleteClient(id: Long) {
        clientDao.deleteClient(id)
    }
}