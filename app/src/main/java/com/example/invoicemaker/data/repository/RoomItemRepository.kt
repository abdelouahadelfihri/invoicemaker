// com.example.invoicemaker.data.local.repository.RoomItemRepository.kt
package com.example.invoicemaker.data.local.repository

import com.example.invoicemaker.data.Item
import com.example.invoicemaker.data.local.dao.ItemDao
import com.example.invoicemaker.data.local.mapper.toDomain
import com.example.invoicemaker.data.local.mapper.toEntity
import com.example.invoicemaker.ui.screens.items.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomItemRepository(
    private val itemDao: ItemDao
) : ItemRepository {
    override fun getAllItemsFlow(): Flow<List<Item>> =
        itemDao.observeAll().map { list -> list.map { it.toDomain() } }
    override suspend fun getItemById(id: Long): Item? = itemDao.getById(id)?.toDomain()
    override suspend fun insertItem(item: Item): Long = itemDao.insert(item.toEntity())
    override suspend fun updateItem(item: Item) = itemDao.update(item.toEntity())
    override suspend fun deleteItem(itemId: Long) { itemDao.getById(itemId)?.let { itemDao.delete(it) } }
}