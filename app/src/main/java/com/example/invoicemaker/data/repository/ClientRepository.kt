package com.example.invoicemaker.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import com.example.invoicemaker.data.local.dao.ClientDao
import com.example.invoicemaker.data.local.entity.Client
import kotlinx.coroutines.flow.Flow

class ClientRepository(private val clientDao: ClientDao) {

    class ClientRepository(private val clientDao: ClientDao) {
        fun observeAll(): Flow<List<Client>> = clientDao.getAllClients()
        fun findClient(client: Client): Flow<List<Client>> = clientDao.findClient(client)
        suspend fun insert(client: Client) = clientDao.insertClient(client)
        suspend fun delete(id: Int) = clientDao.deleteClient(name)
    }
}