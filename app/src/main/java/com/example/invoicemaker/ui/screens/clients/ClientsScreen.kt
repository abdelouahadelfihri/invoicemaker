package com.example.invoicemaker.ui.screens.clients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Simple client model — replace with your Room entity if you already have one
data class Client(
    val id: Long,
    val name: String,
    val email: String? = null,
    val phone: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    clients: List<Client>,
    onAddClient: () -> Unit,
    onSearchClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClientClick: (Client) -> Unit = {}
) {
    Scaffold(
        topBar = {
            ClientsTopBar(
                onSearchClick = onSearchClick,
                onDeleteClick = onDeleteClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClient,
                containerColor = Color(0xFF2E7D32) // green background
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
        if (clients.isEmpty()) {
            EmptyClientsState(modifier = Modifier.padding(innerPadding))
        } else {
            ClientsList(
                clients = clients,
                onClientClick = onClientClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientsTopBar(
    onSearchClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Clients",
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
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tap \"+\" to create new client",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
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
        // spacer so the last item isn't hidden behind the FAB
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
private fun ClientRow(client: Client, onClick: () -> Unit) {
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

// --- Previews ---

@Preview(showBackground = true)
@Composable
private fun ClientsScreenEmptyPreview() {
    MaterialTheme {
        ClientsScreen(
            clients = emptyList(),
            onAddClient = {},
            onSearchClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ClientsScreenListPreview() {
    MaterialTheme {
        ClientsScreen(
            clients = listOf(
                Client(1, "Ahmed Bensaid", phone = "+212 6 12 34 56 78"),
                Client(2, "Fatima Zahra", phone = "+212 6 98 76 54 32")
            ),
            onAddClient = {},
            onSearchClick = {},
            onDeleteClick = {}
        )
    }
}