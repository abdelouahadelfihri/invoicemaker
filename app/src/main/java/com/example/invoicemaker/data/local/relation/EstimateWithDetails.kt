package com.example.invoicemaker.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.invoicemaker.data.local.entity.Client
import com.example.invoicemaker.data.local.entity.EstimateEntity
import com.example.invoicemaker.data.local.entity.EstimateItemEntity

data class EstimateWithDetails(
    @Embedded val estimate: EstimateEntity,

    @Relation(parentColumn = "clientId", entityColumn = "id")
    val client: Client?,

    @Relation(parentColumn = "id", entityColumn = "estimateId")
    val lines: List<EstimateItemEntity>,

    )