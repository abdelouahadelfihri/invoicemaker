package com.example.invoicemaker.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreHoriz

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Invoices : Screen("invoices", "Invoices", Icons.Default.Receipt)
    object Estimates : Screen("estimates", "Estimates", Icons.Default.Description)
    object Clients : Screen("clients", "Clients", Icons.Default.People)
    object Items : Screen("items", "Items", Icons.Default.Inventory2)
    object More : Screen("more", "More", Icons.Default.MoreHoriz)
}

val bottomNavItems = listOf(
    Screen.Invoices,
    Screen.Estimates,
    Screen.Clients,
    Screen.Items,
    Screen.More
)