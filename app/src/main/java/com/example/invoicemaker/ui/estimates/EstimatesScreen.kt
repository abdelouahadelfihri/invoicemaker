package com.example.invoicemaker.ui.estimates

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.invoicemaker.data.InvoiceStatus
import com.example.invoicemaker.ui.components.BobbingHint
import com.example.invoicemaker.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    navController: NavController,
    viewModel: InvoicesViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf<InvoiceStatus?>(null) } // null = "All"

    // TODO: replace with real StateFlow/LiveData collection from InvoicesViewModel
    val allInvoices: List<InvoiceUiModel> = viewModel.invoices.collectAsState(initial = emptyList()).value

    val filteredInvoices = if (selectedFilter == null) {
        allInvoices
    } else {
        allInvoices.filter { it.status == selectedFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoices", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO: open search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* TODO: open filter sheet */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = { /* TODO: open sort menu */ }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (filteredInvoices.isEmpty()) {
                    BobbingHint(text = "Add your first invoice")
                    Spacer(modifier = Modifier.height(4.dp))
                }
                FloatingActionButton(onClick = {
                    // TODO: navController.navigate("add_invoice")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Invoice")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            StatusFilterRow(
                selected = selectedFilter,
                onSelect = { selectedFilter = it }
            )

            if (filteredInvoices.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.ReceiptLong,
                    itemName = "invoice",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredInvoices, key = { it.id }) { invoice ->
                        InvoiceListItem(invoice = invoice, onClick = {
                            // TODO: navController.navigate("invoice_detail/${invoice.id}")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun StatusFilterRow(
    selected: InvoiceStatus?,
    onSelect: (InvoiceStatus?) -> Unit
) {
    val options: List<Pair<String, InvoiceStatus?>> = listOf(
        "All" to null,
        "Unpaid" to InvoiceStatus.UNPAID,
        "Partially Paid" to InvoiceStatus.PARTIALLY_PAID,
        "Overdue" to InvoiceStatus.OVERDUE,
        "Paid" to InvoiceStatus.PAID
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (label, status) ->
            FilterChip(
                selected = selected == status,
                onClick = { onSelect(status) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun InvoiceListItem(invoice: InvoiceUiModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = invoice.clientName,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = invoice.amountFormatted,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = invoice.dueInfo,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (invoice.status == InvoiceStatus.OVERDUE)
                        MaterialTheme.colorScheme.error
                    else
                        Color.Gray
                )
            }

            // Right column
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = invoice.invoiceNumber,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = invoice.issueDateFormatted,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(status = invoice.status)
            }
        }
    }
}

@Composable
fun StatusBadge(status: InvoiceStatus) {
    val (bg, fg) = when (status) {
        InvoiceStatus.UNPAID -> Color(0xFFFFF3CD) to Color(0xFF8A6D3B)
        InvoiceStatus.PARTIALLY_PAID -> Color(0xFFD9EDF7) to Color(0xFF31708F)
        InvoiceStatus.OVERDUE -> Color(0xFFF8D7DA) to Color(0xFF721C24)
        InvoiceStatus.PAID -> Color(0xFFD4EDDA) to Color(0xFF155724)
        InvoiceStatus.CANCELLED -> Color(0xFFE2E3E5) to Color(0xFF383D41)
    }

    Box(
        modifier = Modifier
            .background(bg, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status.label,
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}