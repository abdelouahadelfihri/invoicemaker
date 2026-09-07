package com.example.invoicemaker.ui.screens.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.invoicemaker.data.Item
import com.example.invoicemaker.data.ItemUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

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
    val sortOrder: ItemSortOrder = ItemSortOrder.NAME_ASC
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

    /** Drives `viewModel.items.collectAsState(initial = emptyList())` in Compose. */
    val items: StateFlow<List<Item>> = combine(
        itemRepository.getAllItemsFlow(),
        _filter
    ) { itemList, filter ->
        itemList
            .asSequence()
            .filter { item ->
                filter.query.isBlank() || item.name.contains(filter.query, ignoreCase = true)
            }
            .sortedWith(
                when (filter.sortOrder) {
                    ItemSortOrder.NAME_ASC -> compareBy { it.name.lowercase() }
                    ItemSortOrder.NAME_DESC -> compareByDescending { it.name.lowercase() }
                    ItemSortOrder.PRICE_ASC -> compareBy { it.price ?: BigDecimal.ZERO }
                    ItemSortOrder.PRICE_DESC -> compareByDescending { it.price ?: BigDecimal.ZERO }
                    ItemSortOrder.NEWEST_FIRST -> compareByDescending { it.createdAt }
                }
            )
            .toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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
        _detailState.value = ItemDetailState(item = Item(name = ""))
    }

    fun updateName(name: String) {
        _detailState.update { state ->
            state.item?.let { state.copy(item = it.copy(name = name)) } ?: state
        }
    }

    fun updatePrice(price: BigDecimal?) {
        _detailState.update { state ->
            state.item?.let { state.copy(item = it.copy(price = price)) } ?: state
        }
    }

    fun updateUnit(unit: ItemUnit?) {
        _detailState.update { state ->
            state.item?.let { state.copy(item = it.copy(unit = unit)) } ?: state
        }
    }

    fun updateDescription(description: String) {
        _detailState.update { state ->
            state.item?.let { state.copy(item = it.copy(description = description.ifBlank { null })) } ?: state
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