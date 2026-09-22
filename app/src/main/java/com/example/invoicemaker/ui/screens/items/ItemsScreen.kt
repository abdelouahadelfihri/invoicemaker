package com.example.invoicemaker.ui.screens.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invoicemaker.data.local.entity.Client

private enum class ClientSortOption(val label: String) {
    NAME_ASC("Name (A–Z)"),
    NAME_DESC("Name (Z–A)")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    onAddClient: () -> Unit,
    onDeleteClick: () -> Unit,
    onClientClick: (Client) -> Unit = {},
    viewModel: ItemsViewModel = viewModel()
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf(ClientSortOption.NAME_ASC) }

    val allClients by viewModel.clients.collectAsStateWithLifecycle()

    val filteredClients = allClients
        .filter {
            searchQuery.isBlank() ||
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    (it.phone?.contains(searchQuery, ignoreCase = true) == true)
        }
        .let { list ->
            when (sortOption) {
                ClientSortOption.NAME_ASC -> list.sortedBy { it.name.lowercase() }
                ClientSortOption.NAME_DESC -> list.sortedByDescending { it.name.lowercase() }
            }
        }

    Scaffold(
        topBar = {
            ClientsTopBar(
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearchActiveChange = { active ->
                    isSearchActive = active
                    if (!active) searchQuery = ""
                },
                onDeleteClick = onDeleteClick,
                sortOption = sortOption,
                onSortOptionChange = { sortOption = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClient,
                containerColor = Color(0xFF2E7D32)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add client",
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        if (filteredClients.isEmpty()) {
            EmptyClientsState(modifier = Modifier.padding(innerPadding))
        } else {
            ClientsList(
                clients = filteredClients,
                onClientClick = onClientClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientsTopBar(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    sortOption: ClientSortOption,
    onSortOptionChange: (ClientSortOption) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) focusRequester.requestFocus()
    }

    TopAppBar(
        title = {
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search clients...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            } else {
                Text(text = "Clients", fontWeight = FontWeight.Bold)
            }
        },
        navigationIcon = {
            if (isSearchActive) {
                IconButton(onClick = { onSearchActiveChange(false) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (isSearchActive) {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            } else {
                IconButton(onClick = { onSearchActiveChange(true) }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                Box {
                    IconButton(onClick = { sortMenuExpanded = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        ClientSortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    onSortOptionChange(option)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    )
}

@Composable
private fun EmptyClientsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "No Clients yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Tap \"+\" to create new client",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ClientsList(
    clients: List<Client>,
    onClientClick: (Client) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clients, key = { it.id }) { client ->
            ClientRow(client = client, onClick = { onClientClick(client) })
        }
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
private fun ClientRow(client: Client, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(text = client.name, fontWeight = FontWeight.Medium)
                client.phone?.let {
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClientsListEmptyPreview() {
    MaterialTheme { EmptyClientsState() }
}

@Preview(showBackground = true)
@Composable
private fun ClientsListPreview() {
    MaterialTheme {
        ClientsList(
            clients = listOf(
                Client(id = 1, name = "Ahmed Bensaid", phone = "+212 6 12 34 56 78"),
                Client(id = 2, name = "Fatima Zahra", phone = "+212 6 98 76 54 32")
            ),
            onClientClick = {}
        )
    }
}