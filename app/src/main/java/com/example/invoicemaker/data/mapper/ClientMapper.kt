package com.example.invoicemaker.data.local.mapper

import com.example.invoicemaker.data.Client
import com.example.invoicemaker.data.local.entity.ClientEntity

fun ClientEntity.toDomain(): Client = Client(
    id = id,
    name = name,
    companyName = companyName,
    phone = phone,
    email = email,
    address = address,
    city = city,
    taxId = taxId,
    notes = notes,
    createdAt = createdAt
)

fun Client.toEntity(): ClientEntity = ClientEntity(
    id = id,
    name = name,
    companyName = companyName,
    phone = phone,
    email = email,
    address = address,
    city = city,
    taxId = taxId,
    notes = notes,
    createdAt = createdAt
)