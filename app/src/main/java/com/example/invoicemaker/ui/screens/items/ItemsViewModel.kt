package com.example.invoicemaker.ui.screens.items

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.local.InvoiceDatabase
import com.example.invoicemaker.data.local.entity.Item
import com.example.invoicemaker.data.repository.ItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ItemRepository =
        ItemRepository(InvoiceDatabase.getInstance(application).itemDao())

    val clients: StateFlow<List<Item>> =
        repository.observeAllItems()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun insert(client: Item) {
        viewModelScope.launch { repository.insertItem(client) }
    }

    fun update(client: Item) {
        viewModelScope.launch { repository.updateItem(client) }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repository.deleteItem(id) }
    }
}