package com.example.invoicemaker.ui.screens.documentform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * "Items" (left) / "Price" (right) header, an Add item button, the subtotal
 * row, and one AddableLineRow per extra charge (discount, tax, shipping,
 * advance paid). Used by both the invoice and estimate screens - for an
 * estimate you'd typically drop "advance paid" by passing onAddAdvancePaidClick = null.
 */
@Composable
fun ItemsAndTotalsSection(
    subtotal: String,
    onAddItemClick: () -> Unit,
    onAddDiscountClick: () -> Unit,
    onAddTaxClick: () -> Unit,
    onAddShippingClick: () -> Unit,
    onAddAdvancePaidClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    GroupCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Items", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Price", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Button(
                onClick = onAddItemClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                Text("Add item")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Subtotal", fontSize = 15.sp)
                Text(subtotal, fontSize = 15.sp)
            }

            GroupDivider()

            AddableLineRow(label = "Discount", onAddClick = onAddDiscountClick)
            AddableLineRow(label = "Tax", onAddClick = onAddTaxClick)
            AddableLineRow(label = "Shipping", onAddClick = onAddShippingClick)
            if (onAddAdvancePaidClick != null) {
                AddableLineRow(label = "Advance paid", onAddClick = onAddAdvancePaidClick)
            }
        }
    }
}
