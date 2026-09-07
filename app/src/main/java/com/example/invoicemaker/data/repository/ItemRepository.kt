package com.example.invoicemaker.data.repository

import com.example.invoicemaker.data.Item
import kotlinx.coroutines.flow.Flow

// interface — already inside ItemsViewModel.kt
interface ItemRepository {
    fun getAllItemsFlow(): Flow<List<Item>>
    suspend fun getItemById(id: Long): Item?
    suspend fun insertItem(item: Item): Long
    suspend fun updateItem(item: Item)
    suspend fun deleteItem(itemId: Long)
}