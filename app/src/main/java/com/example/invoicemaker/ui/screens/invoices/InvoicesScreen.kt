package com.example.invoicemaker.ui.screens.invoices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.invoicemaker.data.Invoice
import com.example.invoicemaker.data.InvoiceStatus
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(navController: NavController) {

    var selectedFilter by remember { mutableStateOf<InvoiceStatus?>(null) } // null = "All"

    // TODO: replace with real data from InvoicesViewModel / DBAdapter
    val invoices = remember { sampleInvoices() }

    val filteredInvoices = if (selectedFilter == null) {
        invoices
    } else {
        invoices.filter { it.status == selectedFilter }
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
            FloatingActionButton(onClick = { /* TODO: navigate to Add Invoice screen */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Invoice")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            StatusFilterRow(
                selected = selectedFilter,
                onSelect = { selectedFilter = it }
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
fun InvoiceListItem(invoice: Invoice, onClick: () -> Unit) {
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
                    text = invoice.amount,
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
                    text = invoice.issueDate,
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

// Sample data — remove once wired to DBAdapter/ViewModel
fun sampleInvoices(): List<Invoice> = listOf(
    Invoice(1, "INV-0001", "Ahmed Bennani", "1,250.00 MAD", "Due in 7 days", "12 Jul 2026", InvoiceStatus.UNPAID),
    Invoice(2, "INV-0002", "Sara El Amrani", "3,800.00 MAD", "Overdue by 3 days", "01 Jul 2026", InvoiceStatus.OVERDUE),
    Invoice(3, "INV-0003", "Karim Idrissi", "980.00 MAD", "Paid on 10 Jul", "05 Jul 2026", InvoiceStatus.PAID),
    Invoice(4, "INV-0004", "Yasmine Alaoui", "2,150.00 MAD", "Due in 3 days", "15 Jul 2026", InvoiceStatus.PARTIALLY_PAID)
)