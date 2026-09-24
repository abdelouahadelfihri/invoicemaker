package com.example.invoicemaker.ui.screens.estimates

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invoicemaker.data.local.entity.EstimateStatus
import com.example.invoicemaker.ui.components.BobbingHint
import com.example.invoicemaker.ui.components.EmptyState
import com.example.invoicemaker.ui.screens.invoices.EstimatesViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val estimateDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
private fun formatEstimateDate(millis: Long): String = estimateDateFormatter.format(Date(millis))
private fun formatEstimateMoney(amount: BigDecimal): String =
    NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstimatesScreen(
    onAddEstimate: () -> Unit,
    onEstimateClick: (EstimateUiModel) -> Unit,
    viewModel: EstimatesViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf<EstimateStatus?>(null) } // null = "All"
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val allEstimates: List<EstimateUiModel> by viewModel.estimates.collectAsStateWithLifecycle()

    val filteredEstimates = allEstimates
        .filter { selectedFilter == null || it.status == selectedFilter }
        .filter {
            searchQuery.isBlank() ||
                    it.estimateNumber.contains(searchQuery, ignoreCase = true) ||
                    it.clientName.contains(searchQuery, ignoreCase = true)
        }

    Scaffold(
        topBar = {
            EstimatesTopAppBar(
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
                if (filteredEstimates.isEmpty()) {
                    BobbingHint(text = "Add your first estimate")
                    Spacer(modifier = Modifier.height(4.dp))
                }
                FloatingActionButton(onClick = onAddEstimate) {
                    Icon(Icons.Default.Add, contentDescription = "Add Estimate")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            EstimateStatusFilterRow(
                selected = selectedFilter,
                onSelect = { selectedFilter = it }
            )

            if (filteredEstimates.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Description,
                    itemName = "estimate",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredEstimates, key = { it.id }) { estimate ->
                        EstimateListItem(
                            estimate = estimate,
                            onClick = { onEstimateClick(estimate) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstimatesTopAppBar(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) focusRequester.requestFocus()
    }

    TopAppBar(
        title = {
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search estimates...") },
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
                Text("Estimates", fontWeight = FontWeight.Bold)
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
fun EstimateStatusFilterRow(
    selected: EstimateStatus?,
    onSelect: (EstimateStatus?) -> Unit
) {
    val options: List<Pair<String, EstimateStatus?>> = listOf(
        "All" to null,
        "Draft" to EstimateStatus.DRAFT,
        "Sent" to EstimateStatus.SENT,
        "Accepted" to EstimateStatus.ACCEPTED,
        "Rejected" to EstimateStatus.REJECTED,
        "Expired" to EstimateStatus.EXPIRED
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
fun EstimateListItem(estimate: EstimateUiModel, onClick: () -> Unit) {
    val isExpired = estimate.status == EstimateStatus.EXPIRED

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = estimate.estimateNumber,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = estimate.clientName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                EstimateStatusBadge(status = estimate.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                EstimateInfoCell(
                    label = "Issued",
                    value = formatEstimateDate(estimate.issueDate),
                    modifier = Modifier.weight(1f)
                )
                EstimateInfoCell(
                    label = "Expires",
                    value = formatEstimateDate(estimate.expiryDate),
                    valueColor = if (isExpired) MaterialTheme.colorScheme.error else Color.Unspecified,
                    modifier = Modifier.weight(1f)
                )
                EstimateInfoCell(
                    label = "Items",
                    value = estimate.itemCount.toString(),
                    modifier = Modifier.weight(0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                EstimateInfoCell(
                    label = "Subtotal",
                    value = formatEstimateMoney(estimate.subtotal),
                    modifier = Modifier.weight(1f)
                )
                EstimateInfoCell(
                    label = "Tax",
                    value = formatEstimateMoney(estimate.totalTax),
                    modifier = Modifier.weight(1f)
                )
                EstimateInfoCell(
                    label = "Total",
                    value = formatEstimateMoney(estimate.total),
                    valueWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun EstimateInfoCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified,
    valueWeight: FontWeight = FontWeight.SemiBold
) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = valueWeight,
            color = valueColor
        )
    }
}

@Composable
fun EstimateStatusBadge(status: EstimateStatus) {
    val (bg, fg) = when (status) {
        EstimateStatus.DRAFT -> Color(0xFFE2E3E5) to Color(0xFF383D41)
        EstimateStatus.SENT -> Color(0xFFCCE5FF) to Color(0xFF004085)
        EstimateStatus.ACCEPTED -> Color(0xFFD4EDDA) to Color(0xFF155724)
        EstimateStatus.REJECTED -> Color(0xFFF8D7DA) to Color(0xFF721C24)
        EstimateStatus.EXPIRED -> Color(0xFFE2E3E5) to Color(0xFF6C757D)
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