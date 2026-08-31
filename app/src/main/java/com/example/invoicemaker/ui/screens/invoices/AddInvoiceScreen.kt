package com.example.invoicemaker.ui.screens.invoices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.invoicemaker.ui.screens.documentform.GroupCard
import com.example.invoicemaker.ui.screens.documentform.GroupRow
import com.example.invoicemaker.ui.screens.documentform.GroupDivider
import com.example.invoicemaker.ui.screens.documentform.TemplateThumbnail
import com.example.invoicemaker.ui.screens.documentform.ItemsAndTotalsSection
import com.example.invoicemaker.ui.screens.documentform.PreviewSaveBar

/**
 * State holder for the Add Invoice screen. Wire this up to your ViewModel;
 * shown here as plain fields so the screen compiles standalone.
 */
data class InvoiceFormState(
    val templateName: String = "Select invoice template",
    val language: String = "English",
    val issueDate: String = "Issue date",
    val dueDate: String = "Due date",
    val invoiceNumber: String = "INV00001",
    val businessInfo: String = "Add your business details",
    val clientName: String = "Add client",
    val subtotal: String = "0.00",
    val currency: String = "USD",
    val signature: String = "Add signature",
    val termsSet: Boolean = false,
    val paymentMethodSet: Boolean = false,
    val note: String = "Add note",
    val status: String = "Unpaid",
    val attachmentCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInvoiceScreen(
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
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("New invoice") }) }
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
                    value = "Select invoice language",
                    onClick = onLanguageClick,
                    trailingText = state.language
                )
            }

            // Group 2: invoice info & business info
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

            // Group 3: bill to
            GroupCard {
                GroupRow(
                    label = "Bill to",
                    value = state.clientName,
                    onClick = onBillToClick
                )
            }

            // Group 4: items & totals
            ItemsAndTotalsSection(
                subtotal = state.subtotal,
                onAddItemClick = onAddItemClick,
                onAddDiscountClick = onAddDiscountClick,
                onAddTaxClick = onAddTaxClick,
                onAddShippingClick = onAddShippingClick,
                onAddAdvancePaidClick = onAddAdvancePaidClick
            )

            // Group 5: currency, signature, terms, payment, note, status
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
