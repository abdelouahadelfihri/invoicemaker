package com.example.invoicemaker.ui.screens.invoices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import androidx.compose.material3.OutlinedTextField

data class InvoiceInfo(
    val invoiceNumber: String,
    val issueDate: Long,
    val dueTerms: String,
    val dueDate: Long,
    val poNumber: String,
    val invoiceTitle: String,
    val invoiceNumberLabel: String,
    val invoiceToLabel: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceInfoScreen(
    initialInvoiceNumber: String = "INV00001",
    initialInfo: InvoiceInfo? = null,
    onBack: () -> Unit,
    onSave: (InvoiceInfo) -> Unit
) {

    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    var invoiceNumber by remember {
        mutableStateOf(
            initialInfo?.invoiceNumber ?: initialInvoiceNumber
        )
    }

    var issueDate by remember {
        mutableStateOf(
            initialInfo?.issueDate ?: today
        )
    }

    var dueTerms by remember {
        mutableStateOf(
            initialInfo?.dueTerms ?: "Due on receipt"
        )
    }

    var dueDate by remember {
        mutableStateOf(
            initialInfo?.dueDate ?: today
        )
    }

    var poNumber by remember {
        mutableStateOf(
            initialInfo?.poNumber ?: ""
        )
    }

    var invoiceTitle by remember {
        mutableStateOf(
            initialInfo?.invoiceTitle ?: ""
        )
    }

    var invoiceNumberLabel by remember {
        mutableStateOf(
            initialInfo?.invoiceNumberLabel ?: "INVOICE #"
        )
    }

    var invoiceToLabel by remember {
        mutableStateOf(
            initialInfo?.invoiceToLabel ?: "BILL TO"
        )
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var dueTermsExpanded by remember {
        mutableStateOf(false)
    }

    val dueTermsOptions = listOf(
        "Due on receipt",
        "7 days",
        "15 days",
        "30 days",
        "45 days",
        "60 days",
        "90 days"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Invoice Info",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {

                            val info = InvoiceInfo(
                                invoiceNumber = invoiceNumber,
                                issueDate = issueDate,
                                dueTerms = dueTerms,
                                dueDate = dueDate,
                                poNumber = poNumber,
                                invoiceTitle = invoiceTitle,
                                invoiceNumberLabel = invoiceNumberLabel,
                                invoiceToLabel = invoiceToLabel
                            )

                            onSave(info)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ---------------------------------------------------------
            // BASIC INFORMATION
            // ---------------------------------------------------------

            InvoiceSectionCard(
                title = "Basic Information",
                icon = {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null
                    )
                }
            ) {

                OutlinedTextField(
                    value = invoiceNumber,
                    onValueChange = {
                        invoiceNumber = it
                    },
                    label = {
                        Text("Invoice Number")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = formatDate(issueDate),
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Issue Date")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showDatePicker = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Select issue date"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = dueTermsExpanded,
                    onExpandedChange = {
                        dueTermsExpanded = !dueTermsExpanded
                    }
                ) {

                    OutlinedTextField(
                        value = dueTerms,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Due Terms")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = dueTermsExpanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = dueTermsExpanded,
                        onDismissRequest = {
                            dueTermsExpanded = false
                        }
                    ) {

                        dueTermsOptions.forEach { option ->

                            DropdownMenuItem(
                                text = {
                                    Text(option)
                                },
                                onClick = {

                                    dueTerms = option

                                    dueDate = calculateDueDate(
                                        issueDate,
                                        option
                                    )

                                    dueTermsExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = formatDate(dueDate),
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Due Date")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showDatePicker = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Select due date"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // ---------------------------------------------------------
            // PURCHASE ORDER
            // ---------------------------------------------------------

            InvoiceSectionCard(
                title = "Reference",
                icon = {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null
                    )
                }
            ) {

                OutlinedTextField(
                    value = poNumber,
                    onValueChange = {
                        poNumber = it
                    },
                    label = {
                        Text("P.O.#")
                    },
                    placeholder = {
                        Text("Purchase Order Number")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = invoiceTitle,
                    onValueChange = {
                        invoiceTitle = it
                    },
                    label = {
                        Text("Invoice Title")
                    },
                    placeholder = {
                        Text("Invoice")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // ---------------------------------------------------------
            // LABELS
            // ---------------------------------------------------------

            InvoiceSectionCard(
                title = "Invoice Labels"
            ) {

                OutlinedTextField(
                    value = invoiceNumberLabel,
                    onValueChange = {
                        invoiceNumberLabel = it
                    },
                    label = {
                        Text("Invoice Number Label")
                    },
                    placeholder = {
                        Text("INVOICE #")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = invoiceToLabel,
                    onValueChange = {
                        invoiceToLabel = it
                    },
                    label = {
                        Text("Invoice To Label")
                    },
                    placeholder = {
                        Text("BILL TO")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Optional bottom save button
            Button(
                onClick = {

                    val info = InvoiceInfo(
                        invoiceNumber = invoiceNumber,
                        issueDate = issueDate,
                        dueTerms = dueTerms,
                        dueDate = dueDate,
                        poNumber = poNumber,
                        invoiceTitle = invoiceTitle,
                        invoiceNumberLabel = invoiceNumberLabel,
                        invoiceToLabel = invoiceToLabel
                    )

                    onSave(info)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Save Invoice Info",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // -------------------------------------------------------------
    // DATE PICKER
    // -------------------------------------------------------------

    if (showDatePicker) {

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = issueDate
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState.selectedDateMillis?.let { selectedDate ->

                            issueDate = selectedDate

                            dueDate = calculateDueDate(
                                selectedDate,
                                dueTerms
                            )
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {

            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}


@Composable
private fun InvoiceSectionCard(
    title: String,
    icon: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (icon != null) {

                    icon()

                    Spacer(
                        modifier = Modifier.size(10.dp)
                    )
                }

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            content()
        }
    }
}


private fun formatDate(timestamp: Long): String {

    val formatter = SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.getDefault()
    )

    return formatter.format(Date(timestamp))
}


private fun calculateDueDate(
    issueDate: Long,
    terms: String
): Long {

    val calendar = Calendar.getInstance()

    calendar.timeInMillis = issueDate

    when (terms) {

        "Due on receipt" -> {
            // Same day
        }

        "7 days" -> {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        "15 days" -> {
            calendar.add(Calendar.DAY_OF_YEAR, 15)
        }

        "30 days" -> {
            calendar.add(Calendar.DAY_OF_YEAR, 30)
        }

        "45 days" -> {
            calendar.add(Calendar.DAY_OF_YEAR, 45)
        }

        "60 days" -> {
            calendar.add(Calendar.DAY_OF_YEAR, 60)
        }

        "90 days" -> {
            calendar.add(Calendar.DAY_OF_YEAR, 90)
        }
    }

    return calendar.timeInMillis
}