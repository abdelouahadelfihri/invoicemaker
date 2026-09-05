package com.example.invoicemaker.data.local.entity

import com.example.invoicemaker.data.Client

// ---------------------------------------------------------------------------
// Add this extension function in ClientEntity.kt (or a mappers file).
// Assumes ClientEntity mirrors Client's fields exactly. If your ClientEntity
// has different field names, adjust the right-hand side accordingly.
// ---------------------------------------------------------------------------

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