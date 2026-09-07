package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.Client
import kotlinx.coroutines.flow.Flow

// interface — already inside ItemsViewModel.kt
interface ClientDomainRepository {
    fun getAllClientsFlow(): Flow<List<Client>>
    suspend fun getClientById(id: Long): Client?
    suspend fun insertClient(estimate: Client): Long
    suspend fun updateClient(estimate: Client)
    suspend fun deleteClient(estimateId: Long)
    suspend fun getNextClientNumber(): String
}