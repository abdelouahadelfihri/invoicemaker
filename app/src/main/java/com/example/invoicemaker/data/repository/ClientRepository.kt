package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.local.dao.ClientDao
import com.example.invoicemaker.data.local.entity.Client
import kotlinx.coroutines.flow.Flow

class ClientRepository(private val clientDao: ClientDao) {

    fun observeAll(): Flow<List<Client>> = clientDao.observeAll()

    fun findClient(client: Client): Flow<List<Client>> = clientDao.findClient(client) // UNCONFIRMED: unusual signature, see note above

    suspend fun insert(client: Client) = clientDao.insertClient(client)

    suspend fun delete(id: Long) = clientDao.deleteClient(id) // FIX: was deleteClient(name) with an undefined `name`; renamed + fixed type to Long
}