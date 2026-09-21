package com.example.invoicemaker.ui.screens.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.invoicemaker.data.local.entity.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Repository contract — implement against your Room DAO.
// ---------------------------------------------------------------------------

interface ItemRepository {
    fun getAllItemsFlow(): Flow<List<Item>>
    suspend fun getItemById(id: Long): Item?
    suspend fun insertItem(item: Item): Long
    suspend fun updateItem(item: Item)
    suspend fun deleteItem(itemId: Long)
}

// ---------------------------------------------------------------------------
// List screen filter/sort options
// ---------------------------------------------------------------------------

enum class ItemSortOrder { NAME_ASC, NAME_DESC, PRICE_ASC, PRICE_DESC, NEWEST_FIRST }

data class ItemFilter(
    val query: String = "",
    val sortOrder: ItemSortOrder = ItemSortOrder.NAME_ASC,
    val activeOnly: Boolean = true
)

// ---------------------------------------------------------------------------
// Detail/edit screen state
// ---------------------------------------------------------------------------

data class ItemDetailState(
    val item: Item? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

class ItemsViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    // ---- LIST SCREEN -------------------------------------------------

    private val _filter = MutableStateFlow(ItemFilter())
    val filter: StateFlow<ItemFilter> = _filter.asStateFlow()

    /** Drives `viewModel.items.collectAsStateWithLifecycle()` in Compose. */
    val items: StateFlow<List<Item>> = combine(
        itemRepository.getAllItemsFlow(),
        _filter,
        ::applyFilter
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun applyFilter(itemList: List<Item>, filter: ItemFilter): List<Item> {
        return itemList
            .asSequence()
            .filter { item -> !filter.activeOnly || item.isActive }
            .filter { item ->
                filter.query.isBlank() || item.name.contains(filter.query, ignoreCase = true)
            }
            .sortedWith(
                when (filter.sortOrder) {
                    ItemSortOrder.NAME_ASC -> compareBy { it.name.lowercase() }
                    ItemSortOrder.NAME_DESC -> compareByDescending { it.name.lowercase() }
                    ItemSortOrder.PRICE_ASC -> compareBy { it.unitPrice }
                    ItemSortOrder.PRICE_DESC -> compareByDescending { it.unitPrice }
                    ItemSortOrder.NEWEST_FIRST -> compareByDescending { it.createdAt }
                }
            )
            .toList()
    }

    fun setSearchQuery(query: String) {
        _filter.update { it.copy(query = query) }
    }

    fun setSortOrder(order: ItemSortOrder) {
        _filter.update { it.copy(sortOrder = order) }
    }

    // ---- DETAIL / EDIT SCREEN -----------------------------------------

    private val _detailState = MutableStateFlow(ItemDetailState())
    val detailState: StateFlow<ItemDetailState> = _detailState.asStateFlow()

    fun loadItem(id: Long) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val item = itemRepository.getItemById(id)
                _detailState.update { it.copy(item = item, isLoading = false) }
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load item")
                }
            }
        }
    }

    fun startNewItem() {
        _detailState.value = ItemDetailState(item = Item(name = "", unit = "", unitPrice = 0.0))
    }

    fun updateName(name: String) {
        _detailState.update { state ->
            state.item?.let { state.copy(item = it.copy(name = name)) } ?: state
        }
    }

    fun updatePrice(price: Double) {
        _detailState.update { state ->
            state.item?.let { state.copy(item = it.copy(unitPrice = price)) } ?: state
        }
    }

    fun updateUnit(unit: String) {
        _detailState.update { state ->
            state.item?.let { state.copy(item = it.copy(unit = unit)) } ?: state
        }
    }

    fun updateSku(sku: String) {
        _detailState.update { state ->
            state.item?.let { state.copy(item = it.copy(sku = sku.ifBlank { null })) } ?: state
        }
    }

    // ---- Save / delete --------------------------------------------------

    fun saveItem(onSaved: (Long) -> Unit = {}) {
        val item = _detailState.value.item ?: return
        if (item.name.isBlank()) {
            _detailState.update { it.copy(errorMessage = "Item name is required") }
            return
        }

        viewModelScope.launch {
            _detailState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val itemId = if (item.id == 0L) {
                    itemRepository.insertItem(item)
                } else {
                    itemRepository.updateItem(item)
                    item.id
                }
                _detailState.update { it.copy(isSaving = false) }
                onSaved(itemId)
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "Failed to save item")
                }
            }
        }
    }

    fun deleteItem(id: Long, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                itemRepository.deleteItem(id)
                onDeleted()
            } catch (e: Exception) {
                _detailState.update { it.copy(errorMessage = e.message ?: "Failed to delete item") }
            }
        }
    }

    fun deleteItemFromList(id: Long) {
        viewModelScope.launch { itemRepository.deleteItem(id) }
    }

    fun clearError() {
        _detailState.update { it.copy(errorMessage = null) }
    }

    fun resetDetailState() {
        _detailState.value = ItemDetailState()
    }

    companion object {
        fun factory(itemRepository: ItemRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { ItemsViewModel(itemRepository) }
            }
    }
}