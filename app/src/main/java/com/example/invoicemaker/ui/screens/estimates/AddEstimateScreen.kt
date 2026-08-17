package com.yourapp.ui.documentform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * State holder for the Add Estimate screen. Same shape as InvoiceFormState,
 * minus the fields that don't apply to an estimate (advance paid, payment
 * method), and with wording adjusted (valid-until instead of due date,
 * estimate number instead of invoice number).
 */
data class EstimateFormState(
    val templateName: String = "Select estimate template",
    val language: String = "English",
    val issueDate: String = "Issue date",
    val validUntilDate: String = "Valid until",
    val estimateNumber: String = "EST00001",
    val businessInfo: String = "Add your business details",
    val clientName: String = "Add client",
    val subtotal: String = "0.00",
    val currency: String = "USD",
    val signature: String = "Add signature",
    val termsSet: Boolean = false,
    val note: String = "Add note",
    // Typical estimate lifecycle status, unlike an invoice's paid/unpaid.
    val status: String = "Draft",
    val attachmentCount: Int = 0
)

@Composable
fun AddEstimateScreen(
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
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("New estimate") }) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Group 1: templates & language
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

            // Group 2: estimate info & business info
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

            // Group 3: bill to
            GroupCard {
                GroupRow(
                    label = "Bill to",
                    value = state.clientName,
                    onClick = onBillToClick
                )
            }

            // Group 4: items & totals - no "advance paid" for an estimate
            ItemsAndTotalsSection(
                subtotal = state.subtotal,
                onAddItemClick = onAddItemClick,
                onAddDiscountClick = onAddDiscountClick,
                onAddTaxClick = onAddTaxClick,
                onAddShippingClick = onAddShippingClick,
                onAddAdvancePaidClick = null
            )

            // Group 5: currency, signature, terms, note, status - no "payment method"
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

            // Group 6: attachments
            GroupCard {
                GroupRow(
                    label = "Attachments",
                    value = if (state.attachmentCount == 0) "Add attachment" else "${state.attachmentCount} attached",
                    onClick = onAttachmentAddClick,
                    showArrow = false,
                    trailingText = "Add"
                )
            }

            PreviewSaveBar(onPreview = onPreview, onSave = onSave)
        }
    }
}
