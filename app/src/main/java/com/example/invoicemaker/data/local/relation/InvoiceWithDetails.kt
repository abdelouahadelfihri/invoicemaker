package com.example.invoicemaker.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.yourpackage.invoicemaker.data.local.entity.ClientEntity
import com.yourpackage.invoicemaker.data.local.entity.InvoiceEntity
import com.yourpackage.invoicemaker.data.local.entity.InvoiceLineEntity
import com.yourpackage.invoicemaker.data.local.entity.PaymentEntity

data class InvoiceWithDetails(
    @Embedded val invoice: InvoiceEntity,

    @Relation(parentColumn = "clientId", entityColumn = "id")
    val client: ClientEntity?,

    @Relation(parentColumn = "id", entityColumn = "invoiceId")
    val lines: List<InvoiceLineEntity>,

    @Relation(parentColumn = "id", entityColumn = "invoiceId")
    val payments: List<PaymentEntity>
)