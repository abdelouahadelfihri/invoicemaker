package com.example.invoicemaker.data

data class Client(
    val id: Long = 0,
    val name: String,
    val companyName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val city: String? = null,
    val taxId: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)