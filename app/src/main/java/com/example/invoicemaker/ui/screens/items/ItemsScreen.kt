package com.example.invoicemaker.ui.screens.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Simple item model — replace with your Room entity if you already have one
data class Item(
    val id: Long,
    val name: String,
    val price: Double? = null,
    val unit: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    items: List<Item>,
    onAddItem: () -> Unit,
    onSearchClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onItemClick: (Item) -> Unit = {}
) {
    Scaffold(
        topBar = {
            ItemsTopBar(
                onSearchClick = onSearchClick,
                onDeleteClick = onDeleteClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddItem,
                containerColor = Color(0xFF2E7D32) // green background
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add item",
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        if (items.isEmpty()) {
            EmptyItemsState(modifier = Modifier.padding(innerPadding))
        } else {
            ItemsList(
                items = items,
                onItemClick = onItemClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemsTopBar(
    onSearchClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Items",
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    )
}

@Composable
private fun EmptyItemsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "No Items yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tap \"+\" to create new item",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ItemsList(
    items: List<Item>,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ItemRow(item = item, onClick = { onItemClick(item) })
        }
        // spacer so the last item isn't hidden behind the FAB
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
private fun ItemRow(item: Item, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Medium)
                item.unit?.let {
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item.price?.let {
                Text(
                    text = "%.2f".format(it),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// --- Previews ---

@Preview(showBackground = true)
@Composable
private fun ItemsScreenEmptyPreview() {
    MaterialTheme {
        ItemsScreen(
            items = emptyList(),
            onAddItem = {},
            onSearchClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemsScreenListPreview() {
    MaterialTheme {
        ItemsScreen(
            items = listOf(
                Item(1, "Consulting Hour", price = 450.0, unit = "per hour"),
                Item(2, "Installation Kit", price = 1200.0, unit = "per unit")
            ),
            onAddItem = {},
            onSearchClick = {},
            onDeleteClick = {}
        )
    }
}