package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.Client
import com.example.invoicemaker.data.local.mapper.toDomain
import com.example.invoicemaker.data.local.mapper.toEntity
import com.example.invoicemaker.ui.screens.clients.ClientDomainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClientRepositoryAdapter(
    private val clientRepository: ClientRepository
) : ClientDomainRepository {

    override fun getAllClientsFlow(): Flow<List<Client>> =
        clientRepository.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getClientById(id: Long): Client? =
        clientRepository.getById(id)?.toDomain()

    override suspend fun insertClient(client: Client): Long =
        clientRepository.save(client.toEntity())

    override suspend fun updateClient(client: Client) {
        clientRepository.save(client.toEntity())
    }

    override suspend fun deleteClient(clientId: Long) =
        clientRepository.deleteById(clientId)
}