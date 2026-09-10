package com.example.invoicemaker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.invoicemaker.data.local.AppDatabase
import com.example.invoicemaker.data.local.repository.RoomItemRepository
import com.example.invoicemaker.data.repository.ClientRepository
import com.example.invoicemaker.ui.screens.clients.ClientsScreen
import com.example.invoicemaker.ui.screens.clients.ClientsViewModel
import com.example.invoicemaker.ui.screens.estimates.EstimatesScreen
import com.example.invoicemaker.ui.screens.invoices.InvoicesScreen
import com.example.invoicemaker.ui.screens.items.ItemsScreen
import com.example.invoicemaker.ui.screens.items.ItemsViewModel
import com.example.invoicemaker.ui.screens.more.MoreScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current

    Scaffold(
        bottomBar = { AppBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Invoices.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Invoices.route) {
                InvoicesScreen(navController)
            }
            composable(Screen.Estimates.route) {
                EstimatesScreen(navController)
            }
            composable(Screen.Clients.route) {
                val clientRepository = remember(context) {
                    ClientRepositoryAdapter(
                        ClientRepository(AppDatabase.getInstance(context).clientDao())
                    )
                }
                val clientsViewModel: ClientsViewModel = viewModel(
                    factory = ClientsViewModel.factory(clientRepository)
                )
                val clients by clientsViewModel.clients.collectAsState()

                ClientsScreen(
                    clients = clients,
                    onAddClient = { /* TODO: navController.navigate("add_client") */ },
                    onSearchClick = { /* TODO: open search */ },
                    onDeleteClick = { /* TODO: open delete/select mode */ },
                    onClientClick = { client ->
                        /* TODO: navController.navigate("client_detail/${client.id}") */
                    }
                )
            }
            composable(Screen.Items.route) {
                val itemRepository = remember(context) {
                    RoomItemRepository(AppDatabase.getInstance(context).itemDao())
                }
                val itemsViewModel: ItemsViewModel = viewModel(
                    factory = ItemsViewModel.factory(itemRepository)
                )
                val items by itemsViewModel.items.collectAsState()

                ItemsScreen(
                    items = items,
                    onAddItem = { /* TODO: navController.navigate("add_item") */ },
                    onSearchClick = { /* TODO: open search */ },
                    onDeleteClick = { /* TODO: open delete/select mode */ },
                    onItemClick = { item ->
                        /* TODO: navController.navigate("item_detail/${item.id}") */
                    }
                )
            }
            composable(Screen.More.route) {
                MoreScreen(
                    onNewBusinessClick = { /* TODO: navController.navigate("new_business") */ },
                    onDashboardClick = { /* TODO: navController.navigate("dashboard") */ },
                    onReportClick = { /* TODO: navController.navigate("report") */ },
                    onDeliveryNotesClick = { /* TODO: navController.navigate("delivery_notes") */ },
                    onExpensesClick = { /* TODO: navController.navigate("expenses") */ },
                    onArchivedClick = { /* TODO: navController.navigate("archived") */ },
                    onExportClick = { /* TODO: navController.navigate("export") */ },
                    onSettingsClick = { /* TODO: navController.navigate("settings") */ },
                    onBackupRestoreClick = { /* TODO: navController.navigate("backup_restore") */ },
                    onShareAppClick = { /* TODO: share intent */ },
                    onSupportClick = { /* TODO: navController.navigate("support") */ },
                    onRateUsClick = { /* TODO: open Play Store listing */ }
                )
            }
        }
    }
}