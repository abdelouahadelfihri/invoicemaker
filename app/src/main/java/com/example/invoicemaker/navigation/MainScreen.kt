package com.example.invoicemaker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.invoicemaker.ui.screens.clients.ClientsScreen
import com.example.invoicemaker.ui.screens.estimates.EstimatesScreen
import com.example.invoicemaker.ui.screens.invoices.InvoicesScreen
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
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Invoices.route) {
                InvoicesScreen(
                    onAddInvoice = {
                        navController.navigate("add_invoice")
                    },
                    onInvoiceClick = { invoice ->
                        navController.navigate("invoice_detail/${invoice.id}")
                    }
                )
            }

            composable(Screen.Estimates.route) {
                EstimatesScreen(
                    onAddEstimate = {
                        navController.navigate("add_estimate")
                    },
                    onEstimateClick = { estimate ->
                        navController.navigate("estimate_detail/${estimate.id}")
                    }
                )
            }

            composable(Screen.Clients.route) {
                ClientsScreen(
                    onAddClient = {
                        navController.navigate("add_client")
                    },
                    onDeleteClick = { /* TODO: open delete/select mode */ },
                    onClientClick = { client ->
                        navController.navigate("client_detail/${client.id}")
                    }
                )
            }

            composable(Screen.Items.route) {
                ItemsScreen(
                    onAddItem = {
                        navController.navigate("add_item")
                    },
                    onDeleteClick = { /* TODO: open delete/select mode */ },
                    onItemClick = { item ->
                        navController.navigate("item_detail/${item.id}")
                    }
                )
            }

            composable(Screen.More.route) {
                MoreScreen(
                    onNewBusinessClick = { navController.navigate("new_business") },
                    onDashboardClick = { navController.navigate("dashboard") },
                    onReportClick = { navController.navigate("report") },
                    onDeliveryNotesClick = { navController.navigate("delivery_notes") },
                    onExpensesClick = { navController.navigate("expenses") },
                    onArchivedClick = { navController.navigate("archived") },
                    onExportClick = { navController.navigate("export") },
                    onSettingsClick = { navController.navigate("settings") },
                    onBackupRestoreClick = { navController.navigate("backup_restore") },
                    onShareAppClick = { /* TODO: share intent */ },
                    onSupportClick = { navController.navigate("support") },
                    onRateUsClick = { /* TODO: open Play Store listing */ }
                )
            }
        }
    }
}