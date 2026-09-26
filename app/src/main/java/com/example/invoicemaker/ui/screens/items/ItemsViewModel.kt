package com.example.invoicemaker.ui.screens.items

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.local.InvoiceDatabase
import com.example.invoicemaker.data.local.entity.Item
import com.example.invoicemaker.data.repository.ItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class ItemDetailUiState(
    val item: ItemDraft? = ItemDraft(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

data class ItemDraft(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val unitPrice: BigDecimal? = null,
    val unit: ItemUnit? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class ItemsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ItemRepository =
        ItemRepository(InvoiceDatabase.getInstance(application).itemDao())

    // --- Recherche ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val items: StateFlow<List<Item>> =
        _searchQuery
            .debounce(300)
            .flatMapLatest { query ->
                if (query.isBlank()) repository.observeAllItems()
                else repository.searchItems(query)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- Détail / édition ---
    private val _detailState = MutableStateFlow(ItemDetailUiState())
    val detailState: StateFlow<ItemDetailUiState> = _detailState.asStateFlow()

    fun startNewItem() {
        _detailState.value = ItemDetailUiState(item = ItemDraft())
    }

    fun updateName(name: String) {
        _detailState.update { it.copy(item = it.item?.copy(name = name)) }
    }

    fun updatePrice(price: BigDecimal?) {
        _detailState.update { it.copy(item = it.item?.copy(unitPrice = price)) }
    }

    fun updateUnit(unit: ItemUnit) {
        _detailState.update { it.copy(item = it.item?.copy(unit = unit)) }
    }

    fun updateDescription(description: String) {
        _detailState.update { it.copy(item = it.item?.copy(description = description)) }
    }

    fun clearError() {
        _detailState.update { it.copy(errorMessage = null) }
    }

    fun saveItem(onSaved: (Long) -> Unit) {
        val draft = _detailState.value.item ?: return
        _detailState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                val entity = Item(
                    id = draft.id,
                    name = draft.name,
                    description = draft.description.ifBlank { null },
                    unit = (draft.unit ?: ItemUnit.UNIT).name,
                    unitPrice = (draft.unitPrice ?: BigDecimal.ZERO).toDouble()
                )
                val newId = if (draft.id == 0L) {
                    repository.insertItem(entity)
                } else {
                    repository.updateItem(entity)
                    draft.id
                }
                _detailState.update { it.copy(isSaving = false) }
                onSaved(newId)
            } catch (e: Exception) {
                _detailState.update { it.copy(isSaving = false, errorMessage = e.message ?: "Failed to save item") }
            }
        }
    }

    // --- Suppression ---
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            try {
                repository.deleteItem(item)
            } catch (e: Exception) {
                _detailState.update { it.copy(errorMessage = e.message ?: "Failed to delete item") }
            }
        }
    }
}