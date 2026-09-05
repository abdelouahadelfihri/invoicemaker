package com.example.invoicemaker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.invoicemaker.data.InvoiceStatus
import java.time.LocalDate

/**
 * This is NOT a new standalone table — merge these fields into whatever
 * InvoiceEntity you already have. Unlike BusinessInfoEntity (one row,
 * fixed id), every invoice gets its own row with its own invoice number,
 * dates, etc.
 */
@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // ---- Invoice Info fields (from the Invoice Info screen) ----
    val invoiceNumber: String,       // e.g. "INV00002" — unique per invoice
    val issueDate: LocalDate,
    val dueTerm: String,             // store DueTerm.name, e.g. "NET_30"
    val dueDate: LocalDate,
    val poNumber: String = "",
    val invoiceTitle: String = "Invoice",
    val invoiceNumberLabel: String = "INVOICE #",
    val billToLabel: String = "BILL TO",

    // ---- Your existing invoice fields (client, totals, status, etc.) ----
    val clientId: Long? = null,
    val status: InvoiceStatus = InvoiceStatus.DRAFT
    // subtotal, tax, total, notes, createdAt, etc. — keep whatever else
    // you already had here.
)