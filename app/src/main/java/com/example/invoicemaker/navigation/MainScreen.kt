package com.example.invoicemaker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.invoicemaker.ui.screens.invoices.InvoicesScreen
import com.example.invoicemaker.ui.screens.estimates.EstimatesScreen
import com.example.invoicemaker.ui.screens.clients.ClientsScreen
import com.example.invoicemaker.ui.screens.items.ItemsScreen
import com.example.invoicemaker.ui.screens.more.MoreScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Invoices.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(Screen.Invoices.route) { InvoicesScreen(navController) }
            composable(Screen.Estimates.route) { EstimatesScreen(navController) }
            composable(Screen.Clients.route) { ClientsScreen(navController) }
            composable(Screen.Items.route) { ItemsScreen(navController) }
            composable(Screen.More.route) { MoreScreen(navController) }
        }
    }
}