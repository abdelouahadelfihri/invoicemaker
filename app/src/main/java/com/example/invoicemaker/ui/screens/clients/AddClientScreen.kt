package com.example.invoicemaker.ui.screens.clients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * State holder for the "New Client" form.
 * Kept simple (mutableStateOf per field) so it's easy to wire to a ViewModel later.
 */
class NewClientFormState {
    var fullName by mutableStateOf("")
    var companyName by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var addressLine1 by mutableStateOf("")
    var city by mutableStateOf("")
    var taxId by mutableStateOf("")

    var billingAddress by mutableStateOf("")
    var notes by mutableStateOf("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewClientScreen(
    onBackClick: () -> Unit,
    onSaveClick: (NewClientFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    val formState = remember { NewClientFormState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("New Client") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
            // --- Card 1: seven text fields ---
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
                            value = formState.fullName,
                            onValueChange = { formState.fullName = it },
                            label = "Full name"
                        )
                        FloatingLabelTextField(
                            value = formState.companyName,
                            onValueChange = { formState.companyName = it },
                            label = "Company name"
                        )
                        FloatingLabelTextField(
                            value = formState.email,
                            onValueChange = { formState.email = it },
                            label = "Email"
                        )
                        FloatingLabelTextField(
                            value = formState.phone,
                            onValueChange = { formState.phone = it },
                            label = "Phone"
                        )
                        FloatingLabelTextField(
                            value = formState.addressLine1,
                            onValueChange = { formState.addressLine1 = it },
                            label = "Address"
                        )
                        FloatingLabelTextField(
                            value = formState.city,
                            onValueChange = { formState.city = it },
                            label = "City"
                        )
                        FloatingLabelTextField(
                            value = formState.taxId,
                            onValueChange = { formState.taxId = it },
                            label = "Tax ID"
                        )
                    }
                }
            }

            // --- Card 2: two text areas ---
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
                        FloatingLabelTextArea(
                            value = formState.billingAddress,
                            onValueChange = { formState.billingAddress = it },
                            label = "Billing address"
                        )
                        FloatingLabelTextArea(
                            value = formState.notes,
                            onValueChange = { formState.notes = it },
                            label = "Notes"
                        )
                    }
                }
            }
        }
    }
}

/**
 * Single-line field whose label sits inline as a placeholder and animates
 * to the top-left the moment the field is focused (or already has text).
 * This is the default OutlinedTextField behavior in Material 3 — no
 * extra state needed.
 */
@Composable
private fun FloatingLabelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

/**
 * Multi-line text area with the same floating-label behavior.
 */
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