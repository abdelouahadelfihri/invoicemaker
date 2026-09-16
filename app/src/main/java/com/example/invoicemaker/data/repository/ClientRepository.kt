package com.example.invoicemaker.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import com.example.invoicemaker.data.local.dao.ClientDao
import com.example.invoicemaker.data.local.entity.Client

class ClientRepository(private val productDao: ClientDao) {

    val allClients: LiveData<List<Client>> = productDao.getAllClients()
    val searchResults = MutableLiveData<List<Client>>()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertClient(newproduct: Client) {
        coroutineScope.launch(Dispatchers.IO) {
            productDao.insertClient(newproduct)
        }
    }

    fun deleteClient(name: String) {
        coroutineScope.launch(Dispatchers.IO) {
            productDao.deleteClient(name)
        }
    }

    fun findClient(name: String) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncFind(name).await()
        }
    }

    private fun asyncFind(name: String): Deferred<List<Client>?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async productDao.findClient(name)
        }
}