package com.example.invoicemaker.ui.screens.estimates

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.invoicemaker.data.EstimateStatus
import com.example.invoicemaker.ui.components.BobbingHint
import com.example.invoicemaker.ui.components.EmptyState
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.items
/**
 * UI model derived from Estimate + Client lookup.
 * Build this in the ViewModel by joining Estimate with its Client and
 * pre-formatting values so the Composable stays purely presentational.
 */
data class EstimateUiModel(
    val id: Long,
    val estimateNumber: String,
    val clientName: String,
    val status: EstimateStatus,
    val issueDate: Long,
    val expiryDate: Long,
    val itemCount: Int,
    val subtotal: BigDecimal,
    val totalTax: BigDecimal,
    val total: BigDecimal
) {
    val isOverdue: Boolean
        get() = status !in listOf(EstimateStatus.ACCEPTED, EstimateStatus.REJECTED, EstimateStatus.CONVERTED) &&
                expiryDate < System.currentTimeMillis()
}

private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
private fun formatDate(millis: Long): String = dateFormatter.format(Date(millis))
private fun formatMoney(amount: BigDecimal): String =
    NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstimatesScreen(
    navController: NavController,
    viewModel: EstimatesViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf<EstimateStatus?>(null) } // null = "All"

    val allEstimates: List<EstimateUiModel> = viewModel.estimates.collectAsState(initial = emptyList()).value

    val filteredEstimates = if (selectedFilter == null) {
        allEstimates
    } else {
        allEstimates.filter { it.status == selectedFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estimates", fontWeight = FontWeight.Bold) },
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
                if (filteredEstimates.isEmpty()) {
                    BobbingHint(text = "Add your first estimate")
                    Spacer(modifier = Modifier.height(4.dp))
                }
                FloatingActionButton(onClick = {
                    // TODO: navController.navigate("add_estimate")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Estimate")
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

            if (filteredEstimates.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.ReceiptLong,
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
                        EstimateListItem(estimate = estimate, onClick = {
                            // TODO: navController.navigate("estimate_detail/${estimate.id}")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun StatusFilterRow(
    selected: EstimateStatus?,
    onSelect: (EstimateStatus?) -> Unit
) {
    val options: List<Pair<String, EstimateStatus?>> = listOf(
        "All" to null,
        "Draft" to EstimateStatus.DRAFT,
        "Sent" to EstimateStatus.SENT,
        "Accepted" to EstimateStatus.ACCEPTED,
        "Rejected" to EstimateStatus.REJECTED,
        "Expired" to EstimateStatus.EXPIRED,
        "Converted" to EstimateStatus.CONVERTED
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
 * labeled fields so the user gets client, dates, item count and the
 * full money breakdown without opening the estimate.
 */
@Composable
fun EstimateListItem(estimate: EstimateUiModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Header: estimate number, client name, status badge
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
                StatusBadge(status = estimate.status, isOverdue = estimate.isOverdue)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Tabular info grid: two rows of label/value pairs
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoCell(
                    label = "Issued",
                    value = formatDate(estimate.issueDate),
                    modifier = Modifier.weight(1f)
                )
                InfoCell(
                    label = "Expires",
                    value = formatDate(estimate.expiryDate),
                    valueColor = if (estimate.isOverdue) MaterialTheme.colorScheme.error else Color.Unspecified,
                    modifier = Modifier.weight(1f)
                )
                InfoCell(
                    label = "Items",
                    value = estimate.itemCount.toString(),
                    modifier = Modifier.weight(0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                InfoCell(
                    label = "Subtotal",
                    value = formatMoney(estimate.subtotal),
                    modifier = Modifier.weight(1f)
                )
                InfoCell(
                    label = "Tax",
                    value = formatMoney(estimate.totalTax),
                    modifier = Modifier.weight(1f)
                )
                InfoCell(
                    label = "Total",
                    value = formatMoney(estimate.total),
                    valueColor = MaterialTheme.colorScheme.primary,
                    valueWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            if (estimate.isOverdue) {
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
                        text = "Expired — follow up with client",
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
fun StatusBadge(status: EstimateStatus, isOverdue: Boolean = false) {
    val (bg, fg) = when {
        isOverdue -> Color(0xFFF8D7DA) to Color(0xFF721C24)
        status == EstimateStatus.DRAFT -> Color(0xFFE2E3E5) to Color(0xFF383D41)
        status == EstimateStatus.SENT -> Color(0xFFD9EDF7) to Color(0xFF31708F)
        status == EstimateStatus.ACCEPTED -> Color(0xFFD4EDDA) to Color(0xFF155724)
        status == EstimateStatus.REJECTED -> Color(0xFFF8D7DA) to Color(0xFF721C24)
        status == EstimateStatus.EXPIRED -> Color(0xFFFFF3CD) to Color(0xFF8A6D3B)
        status == EstimateStatus.CONVERTED -> Color(0xFFCCE5FF) to Color(0xFF004085)
        else -> Color(0xFFE2E3E5) to Color(0xFF383D41)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (isOverdue) "Overdue" else status.label,
            color = fg,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}