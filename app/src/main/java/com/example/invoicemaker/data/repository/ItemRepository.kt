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

    suspend fun insertItem(item: Item): Long =
        itemDao.insert(item)

    suspend fun updateItem(item: Item) {
        itemDao.update(item)
    }

    // Repository
    suspend fun deleteItem(item: Item) = itemDao.delete(item)
}