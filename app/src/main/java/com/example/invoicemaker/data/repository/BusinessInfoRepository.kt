package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.local.dao.ClientDao
import com.example.invoicemaker.data.local.entity.Client
import kotlinx.coroutines.flow.Flow

class BusinessInfoRepository(private val businessDao: ClientDao) {

    suspend fun insert(client: Client) = businessDao.insertClient(client)

    suspend fun delete(id: Long) = businessDao.deleteClient(id) // FIX: was deleteClient(name) with an undefined `name`; renamed + fixed type to Long
}