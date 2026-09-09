package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.Client
import kotlinx.coroutines.flow.Flow

interface ClientDomainRepository {
    fun getAllClientsFlow(): Flow<List<Client>>
    suspend fun getClientById(id: Long): Client?
    suspend fun insertClient(client: Client): Long
    suspend fun updateClient(client: Client)
    suspend fun deleteClient(clientId: Long)
}