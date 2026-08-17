package com.yourapp.ui.documentform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Edit screen for an existing invoice. Same layout as AddInvoiceScreen -
 * pass in the InvoiceFormState loaded from your DB/ViewModel for the
 * invoice being edited, and it renders pre-filled. The only UI differences
 * from the add screen are the "Edit invoice" title, a delete action in the
 * top bar, and the bottom button reading "Update" instead of "Save".
 */
@Composable
fun UpdateInvoiceScreen(
    state: InvoiceFormState,
    onTemplateClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onInvoiceInfoClick: () -> Unit,
    onBusinessInfoClick: () -> Unit,
    onBillToClick: () -> Unit,
    onAddItemClick: () -> Unit,
    onAddDiscountClick: () -> Unit,
    onAddTaxClick: () -> Unit,
    onAddShippingClick: () -> Unit,
    onAddAdvancePaidClick: () -> Unit,
    onCurrencyClick: () -> Unit,
    onSignatureClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPaymentMethodClick: () -> Unit,
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
                title = { Text("Edit invoice") },
                actions = {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete invoice")
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
                    value = "Select invoice language",
                    onClick = onLanguageClick,
                    trailingText = state.language
                )
            }

            GroupCard {
                GroupRow(
                    label = "Invoice info",
                    value = "${state.issueDate}  ·  ${state.dueDate}",
                    onClick = onInvoiceInfoClick,
                    trailingText = state.invoiceNumber
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
                onAddAdvancePaidClick = onAddAdvancePaidClick
            )

            GroupCard {
                GroupRow(label = null, value = "Currency", onClick = onCurrencyClick, trailingText = state.currency)
                GroupDivider()
                GroupRow(label = null, value = "Signature", onClick = onSignatureClick, trailingText = state.signature, showArrow = false)
                GroupDivider()
                GroupRow(label = null, value = "Terms and conditions", onClick = onTermsClick)
                GroupDivider()
                GroupRow(label = null, value = "Payment method", onClick = onPaymentMethodClick)
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
