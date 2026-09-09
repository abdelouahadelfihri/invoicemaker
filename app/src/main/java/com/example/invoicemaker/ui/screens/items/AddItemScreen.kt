package com.example.invoicemaker.ui.screens.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.invoicemaker.data.ItemUnit
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewItemScreen(
    onBackClick: () -> Unit,
    onItemSaved: (Long) -> Unit,
    viewModel: ItemsViewModel,
    modifier: Modifier = Modifier
) {
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val item = detailState.item

    // Local text buffer for the price field so the user can type freely
    // (e.g. "12." or "12,5") before it's parsed into a BigDecimal.
    var priceText by remember(item?.price) {
        mutableStateOf(item?.price?.toPlainString().orEmpty())
    }

    LaunchedEffect(Unit) {
        viewModel.startNewItem()
    }

    LaunchedEffect(detailState.errorMessage) {
        val message = detailState.errorMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("New Item") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (detailState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .height(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { viewModel.saveItem(onSaved = onItemSaved) }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save item"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Card 1: three fields ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FloatingLabelTextField(
                            value = item?.name.orEmpty(),
                            onValueChange = viewModel::updateName,
                            label = "Item name"
                        )
                        FloatingLabelTextField(
                            value = priceText,
                            onValueChange = { text ->
                                priceText = text
                                text.replace(",", ".").toBigDecimalOrNull()?.let {
                                    viewModel.updatePrice(it)
                                } ?: viewModel.updatePrice(null)
                            },
                            label = "Item price",
                            keyboardType = KeyboardType.Decimal
                        )
                        UnitDropdown(
                            selected = item?.unit,
                            onSelected = viewModel::updateUnit
                        )
                    }
                }
            }

            // --- Card 2: description text area ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        FloatingLabelTextArea(
                            value = item?.description.orEmpty(),
                            onValueChange = viewModel::updateDescription,
                            label = "Item description"
                        )
                    }
                }
            }
        }
    }
}

/** Dropdown built from the ItemUnit enum — swap the label logic if you add a display name to it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(
    selected: ItemUnit?,
    onSelected: (ItemUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected?.name.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Unit of measure") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuDefaults.PrimaryEditable)
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ItemUnit.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit.name) },
                    onClick = {
                        onSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Single-line field whose label sits inline as a placeholder and animates
 * to the top-left the moment the field is focused (or already has text).
 */
@Composable
private fun FloatingLabelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

/** Multi-line text area with the same floating-label behavior. */
@Composable
private fun FloatingLabelTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 4
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = false,
        minLines = minLines,
        modifier = modifier
            .fillMaxWidth()
            .height((minLines * 24 + 32).dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}