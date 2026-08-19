package com.example.invoicemaker.ui.screens.invoices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.filled.DateRange

// ---------- State for this screen ----------

enum class DueTerm(val label: String, val days: Long?) {
    ON_RECEIPT("Due on Receipt", 0),
    NET_7("Net 7", 7),
    NET_15("Net 15", 15),
    NET_30("Net 30", 30),
    NET_60("Net 60", 60),
    CUSTOM("Custom", null)
}

data class InvoiceInfoState(
    val invoiceNumber: String = "",
    val issueDate: LocalDate = LocalDate.now(),
    val dueTerm: DueTerm = DueTerm.NET_30,
    val dueDate: LocalDate = LocalDate.now().plusDays(30),
    val poNumber: String = "",
    val invoiceTitle: String = "Invoice",
    val invoiceNumberLabel: String = "INVOICE #",
    val billToLabel: String = "BILL TO"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceInfoScreen(
    // Pass in the next auto-generated number, e.g. "INV00002",
    // computed by your ViewModel from the last saved invoice in Room
    // (last number + 1, zero-padded to 5 digits).
    nextInvoiceNumber: String,
    onBack: () -> Unit,
    onValidate: (InvoiceInfoState) -> Unit
) {
    var state by remember {
        mutableStateOf(InvoiceInfoState(invoiceNumber = nextInvoiceNumber))
    }

    Scaffold(
        topBar = {
            InvoiceInfoTopBar(
                onBack = onBack,
                onValidate = { onValidate(state) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Invoice Number — pre-filled from the DB, still
                    // editable in case the user wants to override it.
                    OutlinedTextField(
                        value = state.invoiceNumber,
                        onValueChange = { state = state.copy(invoiceNumber = it) },
                        label = { Text("Invoice Number") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    // Issue Date — opens a Material3 calendar dialog
                    DatePickerField(
                        label = "Issue Date",
                        date = state.issueDate,
                        onDateSelected = { newDate ->
                            state = state.copy(
                                issueDate = newDate,
                                dueDate = state.dueTerm.days?.let { newDate.plusDays(it) }
                                    ?: state.dueDate
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    // Due Terms — spinner, built as an ExposedDropdownMenu
                    DueTermDropdown(
                        selected = state.dueTerm,
                        onSelected = { term ->
                            state = state.copy(
                                dueTerm = term,
                                dueDate = term.days?.let { state.issueDate.plusDays(it) }
                                    ?: state.dueDate
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    // Due Date — auto-computed from Issue Date + Due Term,
                    // but still user-editable via the same calendar picker
                    // (e.g. when Due Term is "Custom").
                    DatePickerField(
                        label = "Due Date",
                        date = state.dueDate,
                        onDateSelected = { newDate -> state = state.copy(dueDate = newDate) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    // P.O.# — Purchase Order Number: a reference number
                    // the client/buyer gave you, tied to their own internal
                    // purchase-order for this job. Optional; many invoices
                    // leave it blank unless the client specifically requires it.
                    OutlinedTextField(
                        value = state.poNumber,
                        onValueChange = { state = state.copy(poNumber = it) },
                        label = { Text("P.O.#") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = state.invoiceTitle,
                        onValueChange = { state = state.copy(invoiceTitle = it) },
                        label = { Text("Invoice Title") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    // Customizable label printed above the invoice number
                    // on the generated PDF (defaults to "INVOICE #").
                    OutlinedTextField(
                        value = state.invoiceNumberLabel,
                        onValueChange = { state = state.copy(invoiceNumberLabel = it) },
                        label = { Text("Invoice Number Label") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    // Customizable label printed above the client's
                    // name/address on the generated PDF (defaults to "BILL TO").
                    OutlinedTextField(
                        value = state.billToLabel,
                        onValueChange = { state = state.copy(billToLabel = it) },
                        label = { Text("Invoice To Label") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvoiceInfoTopBar(
    onBack: () -> Unit,
    onValidate: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Invoice Info",
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onValidate) {
                Icon(Icons.Default.Check, contentDescription = "Validate")
            }
        }
    )
}

// ---------- Reusable date-picker field ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    label: String,
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    OutlinedTextField(
        value = date.format(formatter),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick date")
            }
        },
        modifier = modifier
    )

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val picked = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(picked)
                    }
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = true
            )
        }
    }
}

// ---------- Due Terms spinner ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DueTermDropdown(
    selected: DueTerm,
    onSelected: (DueTerm) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Due Terms") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DueTerm.values().forEach { term ->
                DropdownMenuItem(
                    text = { Text(term.label) },
                    onClick = {
                        onSelected(term)
                        expanded = false
                    }
                )
            }
        }
    }
}