package com.example.invoicemaker.ui.screens.invoices

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.invoicemaker.data.local.entity.InvoiceStatus
import com.example.invoicemaker.ui.components.BobbingHint
import com.example.invoicemaker.ui.components.EmptyState
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.items

data class InvoiceUiModel(
    val id: Long,
    val invoiceNumber: String,
    val clientName: String,
    val status: InvoiceStatus,       // pass in invoice.computedStatus
    val issueDate: Long,
    val dueDate: Long,
    val itemCount: Int,
    val paymentCount: Int,
    val subtotal: BigDecimal,
    val totalTax: BigDecimal,
    val total: BigDecimal,
    val amountPaid: BigDecimal,
    val amountDue: BigDecimal
)

private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
private fun formatDate(millis: Long): String = dateFormatter.format(Date(millis))
private fun formatMoney(amount: BigDecimal): String =
    NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    navController: NavController,
    viewModel: InvoicesViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf<InvoiceStatus?>(null) } // null = "All"
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val allInvoices: List<InvoiceUiModel> = viewModel.invoices.collectAsState(initial = emptyList()).value

    val filteredInvoices = allInvoices
        .filter { selectedFilter == null || it.status == selectedFilter }
        .filter {
            searchQuery.isBlank() ||
                    it.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                    it.clientName.contains(searchQuery, ignoreCase = true)
        }

    Scaffold(
        topBar = {
            InvoicesTopAppBar(
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearchActiveChange = { active ->
                    isSearchActive = active
                    if (!active) searchQuery = ""
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

/**
 * Top bar that toggles between its normal state (title + search/filter/sort icons)
 * and an active search state (back arrow + text field + clear button).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesTopAppBar(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            focusRequester.requestFocus()
        }
    }

    TopAppBar(
        title = {
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search invoices...") },
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
                Text("Invoices", fontWeight = FontWeight.Bold)
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
                IconButton(onClick = { /* TODO: open filter sheet */ }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter")
                }
                IconButton(onClick = { /* TODO: open sort menu */ }) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort")
                }
            }
        }
    )
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
        "Paid" to InvoiceStatus.PAID,
        "Cancelled" to InvoiceStatus.CANCELLED
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

/**
 * Tabular-style card: header row (number + status), then a grid of
 * labeled fields — dates, item/payment counts, and the full money
 * breakdown including what's still owed.
 */
@Composable
fun InvoiceListItem(invoice: InvoiceUiModel, onClick: () -> Unit) {
    val isOverdue = invoice.status == InvoiceStatus.OVERDUE

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Header: invoice number, client name, status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = invoice.invoiceNumber,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = invoice.clientName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                StatusBadge(status = invoice.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Row 1: dates + counts
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoCell(
                    label = "Issued",
                    value = formatDate(invoice.issueDate),
                    modifier = Modifier.weight(1f)
                )
                InfoCell(
                    label = "Due",
                    value = formatDate(invoice.dueDate),
                    valueColor = if (isOverdue) MaterialTheme.colorScheme.error else Color.Unspecified,
                    modifier = Modifier.weight(1f)
                )
                InfoCell(
                    label = "Items",
                    value = invoice.itemCount.toString(),
                    modifier = Modifier.weight(0.5f)
                )
                InfoCell(
                    label = "Payments",
                    value = invoice.paymentCount.toString(),
                    modifier = Modifier.weight(0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: money breakdown, including what's paid vs. still owed
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoCell(
                    label = "Total",
                    value = formatMoney(invoice.total),
                    modifier = Modifier.weight(1f)
                )
                InfoCell(
                    label = "Paid",
                    value = formatMoney(invoice.amountPaid),
                    valueColor = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                InfoCell(
                    label = "Due",
                    value = formatMoney(invoice.amountDue),
                    valueColor = if (invoice.amountDue > BigDecimal.ZERO)
                        MaterialTheme.colorScheme.error
                    else
                        Color(0xFF2E7D32),
                    valueWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            if (isOverdue) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Past due — ${formatMoney(invoice.amountDue)} outstanding",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified,
    valueWeight: FontWeight = FontWeight.SemiBold
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = valueWeight,
            color = valueColor
        )
    }
}

@Composable
fun StatusBadge(status: InvoiceStatus) {
    val (bg, fg) = when (status) {
        InvoiceStatus.UNPAID -> Color(0xFFE2E3E5) to Color(0xFF383D41)
        InvoiceStatus.PARTIALLY_PAID -> Color(0xFFFFF3CD) to Color(0xFF8A6D3B)
        InvoiceStatus.OVERDUE -> Color(0xFFF8D7DA) to Color(0xFF721C24)
        InvoiceStatus.PAID -> Color(0xFFD4EDDA) to Color(0xFF155724)
        InvoiceStatus.CANCELLED -> Color(0xFFE2E3E5) to Color(0xFF6C757D)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}