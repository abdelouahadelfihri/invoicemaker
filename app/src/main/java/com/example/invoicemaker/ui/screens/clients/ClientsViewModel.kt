package com.example.invoicemaker.ui.screens.clients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.invoicemaker.data.local.entity.Client

class ClientsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClientRepository
    val allClients: LiveData<List<Client>>
    val searchResults: MutableLiveData<List<Client>>

    init {
        val productDb = ClientRoomDatabase.getInstance(application)
        val productDao = productDb.productDao()
        repository = ClientRepository(productDao)

        allClients = repository.allClients
        searchResults = repository.searchResults
    }

    fun insertClient(product: Client) {
        repository.insertClient(product)
    }

    fun findClient(name: String) {
        repository.findClient(name)
    }

    fun deleteClient(name: String) {
        repository.deleteClient(name)
    }
}