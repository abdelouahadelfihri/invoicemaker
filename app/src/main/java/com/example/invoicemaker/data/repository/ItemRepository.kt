package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.local.dao.ItemDao
import com.example.invoicemaker.data.local.entity.Item
import kotlinx.coroutines.flow.Flow

class ItemRepository(
    private val itemDao: ItemDao
) {

    fun observeAllItems(): Flow<List<Item>> =
        itemDao.observeAll()

    fun searchItems(query: String): Flow<List<Item>> =
        itemDao.observeSearch(query)

    suspend fun insertItem(client: Item) {
        itemDao.insert(client)
    }

    suspend fun updateItem(client: Item) {
        itemDao.update(client)
    }

    suspend fun deleteItem(id: Long) {
        itemDao.delete(id)
    }
}