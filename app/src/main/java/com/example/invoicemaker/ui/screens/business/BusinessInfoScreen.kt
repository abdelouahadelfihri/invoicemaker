package com.example.invoicemaker.ui.screens.business

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Simple holder for all Business Info fields.
// Swap this for your ViewModel's UiState when you wire it up.
data class BusinessInfoState(
    val businessName: String = "",
    val email: String = "",
    val phone: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val website: String = "",
    val taxName: String = "",
    val taxId: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessInfoScreen(
    onBack: () -> Unit,
    onValidate: (BusinessInfoState) -> Unit,
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit
) {
    var state by remember { mutableStateOf(BusinessInfoState()) }
    var showLogoDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            BusinessInfoTopBar(
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo picker button
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { showLogoDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Business Logo",
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Business Logo",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 8 floating-label text fields.
                    // OutlinedTextField's `label` floats up automatically
                    // once the field is focused or non-empty.
                    FloatingLabelField(
                        value = state.businessName,
                        onValueChange = { state = state.copy(businessName = it) },
                        label = "Business Name"
                    )
                    FloatingLabelField(
                        value = state.email,
                        onValueChange = { state = state.copy(email = it) },
                        label = "Email Address"
                    )
                    FloatingLabelField(
                        value = state.phone,
                        onValueChange = { state = state.copy(phone = it) },
                        label = "Phone Number"
                    )
                    FloatingLabelField(
                        value = state.addressLine1,
                        onValueChange = { state = state.copy(addressLine1 = it) },
                        label = "Billing Address Line 1"
                    )
                    FloatingLabelField(
                        value = state.addressLine2,
                        onValueChange = { state = state.copy(addressLine2 = it) },
                        label = "Billing Address Line 2"
                    )
                    FloatingLabelField(
                        value = state.website,
                        onValueChange = { state = state.copy(website = it) },
                        label = "Business Website"
                    )
                    FloatingLabelField(
                        value = state.taxName,
                        onValueChange = { state = state.copy(taxName = it) },
                        label = "Tax Name (e.g. EIN, VAT, TIN)"
                    )
                    FloatingLabelField(
                        value = state.taxId,
                        onValueChange = { state = state.copy(taxId = it) },
                        label = "Tax ID",
                        lastField = true
                    )
                }
            }
        }
    }

    if (showLogoDialog) {
        BusinessLogoDialog(
            onDismiss = { showLogoDialog = false },
            onFromGallery = {
                showLogoDialog = false
                onPickFromGallery()
            },
            onTakePhoto = {
                showLogoDialog = false
                onTakePhoto()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BusinessInfoTopBar(
    onBack: () -> Unit,
    onValidate: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Business Info",
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

@Composable
private fun BusinessLogoDialog(
    onDismiss: () -> Unit,
    onFromGallery: () -> Unit,
    onTakePhoto: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Business Logo") },
        text = {
            Column {
                LogoDialogItem(
                    icon = Icons.Default.Image,
                    label = "From Gallery",
                    onClick = onFromGallery
                )
                Spacer(modifier = Modifier.height(4.dp))
                LogoDialogItem(
                    icon = Icons.Default.CameraAlt,
                    label = "Take Photo",
                    onClick = onTakePhoto
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF2E7D32)
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun LogoDialogItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, fontSize = 16.sp)
    }
}

@Composable
private fun FloatingLabelField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    lastField: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (lastField) 0.dp else 12.dp)
    )
}