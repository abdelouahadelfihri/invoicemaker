package com.example.invoicemaker.ui.screens.estimates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.invoicemaker.ui.screens.documentform.GroupDivider
import com.example.invoicemaker.ui.screens.documentform.GroupRow
import com.example.invoicemaker.ui.screens.documentform.GroupCard
import com.example.invoicemaker.ui.screens.documentform.PreviewSaveBar
import com.example.invoicemaker.ui.screens.documentform.ItemsAndTotalsSection
import com.example.invoicemaker.ui.screens.documentform.TemplateThumbnail

/**
 * Edit screen for an existing estimate. Same layout as AddEstimateScreen -
 * pass in the EstimateFormState loaded from your DB/ViewModel for the
 * estimate being edited. Differences from the add screen: "Edit estimate"
 * title, a delete action in the top bar, and "Update" instead of "Save".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateEstimateScreen(
    state: EstimateFormState,
    onTemplateClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onEstimateInfoClick: () -> Unit,
    onBusinessInfoClick: () -> Unit,
    onBillToClick: () -> Unit,
    onAddItemClick: () -> Unit,
    onAddDiscountClick: () -> Unit,
    onAddTaxClick: () -> Unit,
    onAddShippingClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onSignatureClick: () -> Unit,
    onTermsClick: () -> Unit,
    onNoteClick: () -> Unit,
    onMarkAsClick: () -> Unit,
    onAttachmentAddClick: () -> Unit,
    onPreview: () -> Unit,
    onUpdate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit estimate") },
                actions = {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete estimate")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GroupCard {
                GroupRow(
                    label = "Templates",
                    value = state.templateName,
                    onClick = onTemplateClick,
                    trailing = { TemplateThumbnail() }
                )
                GroupDivider()
                GroupRow(
                    label = "Language",
                    value = "Select estimate language",
                    onClick = onLanguageClick,
                    trailingText = state.language
                )
            }

            GroupCard {
                GroupRow(
                    label = "Estimate info",
                    value = "${state.issueDate}  ·  ${state.validUntilDate}",
                    onClick = onEstimateInfoClick,
                    trailingText = state.estimateNumber
                )
                GroupDivider()
                GroupRow(
                    label = "Business info",
                    value = state.businessInfo,
                    onClick = onBusinessInfoClick
                )
            }

            GroupCard {
                GroupRow(
                    label = "Bill to",
                    value = state.clientName,
                    onClick = onBillToClick
                )
            }

            ItemsAndTotalsSection(
                subtotal = state.subtotal,
                onAddItemClick = onAddItemClick,
                onAddDiscountClick = onAddDiscountClick,
                onAddTaxClick = onAddTaxClick,
                onAddShippingClick = onAddShippingClick,
                onAddAdvancePaidClick = null
            )

            GroupCard {
                GroupRow(label = null, value = "Currency", onClick = onCurrencyClick, trailingText = state.currency)
                GroupDivider()
                GroupRow(label = null, value = "Signature", onClick = onSignatureClick, trailingText = state.signature, showArrow = false)
                GroupDivider()
                GroupRow(label = null, value = "Terms and conditions", onClick = onTermsClick)
                GroupDivider()
                GroupRow(label = null, value = "Note", onClick = onNoteClick, trailingText = state.note, showArrow = false)
                GroupDivider()
                GroupRow(label = null, value = "Mark as", onClick = onMarkAsClick, trailingText = state.status)
            }

            GroupCard {
                GroupRow(
                    label = "Attachments",
                    value = if (state.attachmentCount == 0) "Add attachment" else "${state.attachmentCount} attached",
                    onClick = onAttachmentAddClick,
                    showArrow = false,
                    trailingText = "Add"
                )
            }

            PreviewSaveBar(onPreview = onPreview, onSave = onUpdate, saveLabel = "Update")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UpdateEstimateScreenPreview() {
    UpdateEstimateScreen(
        state = EstimateFormState(
            templateName = "Modern template",
            issueDate = "Aug 29, 2026",
            validUntilDate = "Sep 29, 2026",
            estimateNumber = "EST00123",
            businessInfo = "Orange Business Services",
            clientName = "Acme Corp",
            subtotal = "1,250.00",
            currency = "USD",
            signature = "Signed",
            termsSet = true,
            note = "Thank you for your business",
            status = "Sent",
            attachmentCount = 2
        ),
        onTemplateClick = {},
        onLanguageClick = {},
        onEstimateInfoClick = {},
        onBusinessInfoClick = {},
        onBillToClick = {},
        onAddItemClick = {},
        onAddDiscountClick = {},
        onAddTaxClick = {},
        onAddShippingClick = {},
        onCurrencyClick = {},
        onSignatureClick = {},
        onTermsClick = {},
        onNoteClick = {},
        onMarkAsClick = {},
        onAttachmentAddClick = {},
        onPreview = {},
        onUpdate = {},
        onDelete = {}
    )
}