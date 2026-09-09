package com.example.invoicemaker.ui.screens.clients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * NOTE: Client (per your model) currently exposes name / email / phone /
 * address / notes. That's 3 single-line fields + 2 text areas below — if
 * you also want company name, city, or tax ID on this screen, those need
 * to be added to the Client data class and to ClientsViewModel's
 * update save logic first; happy to wire that in once they're there.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewClientScreen(
    onBackClick: () -> Unit,
    onClientSaved: (Long) -> Unit,
    viewModel: ClientsViewModel,
    modifier: Modifier = Modifier
) {
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val client = detailState.client

    // Start a fresh, empty client the first time this screen is shown.
    LaunchedEffect(Unit) {
        viewModel.startNewClient()
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
                title = { Text("New Client") },
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
                        IconButton(onClick = { viewModel.saveClient(onSaved = onClientSaved) }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save client"
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
            // --- Card 1: single-line fields ---
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
                            value = client?.name.orEmpty(),
                            onValueChange = viewModel::updateName,
                            label = "Full name"
                        )
                        FloatingLabelTextField(
                            value = client?.email.orEmpty(),
                            onValueChange = viewModel::updateEmail,
                            label = "Email"
                        )
                        FloatingLabelTextField(
                            value = client?.phone.orEmpty(),
                            onValueChange = viewModel::updatePhone,
                            label = "Phone"
                        )
                    }
                }
            }

            // --- Card 2: text areas ---
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
                            value = client?.address.orEmpty(),
                            onValueChange = viewModel::updateAddress,
                            label = "Address"
                        )
                        FloatingLabelTextArea(
                            value = client?.notes.orEmpty(),
                            onValueChange = viewModel::updateNotes,
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
 * This is the default OutlinedTextField behavior in Material 3.
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