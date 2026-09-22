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

    val items: StateFlow<List<Item>> =
        repository.observeAllItems()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun insert(item: Item) {
        viewModelScope.launch { repository.insertItem(item) }
    }

    fun update(item: Item) {
        viewModelScope.launch { repository.updateItem(item) }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repository.deleteItem(id) }
    }
}