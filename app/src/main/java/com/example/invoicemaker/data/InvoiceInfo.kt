package com.example.invoicemaker.data

data class InvoiceInfo(
    val invoiceNumber: String = "",
    val issueDate: Long = 0L,
    val dueTerms: String = "",
    val dueDate: Long = 0L,
    val poNumber: String = "",
    val invoiceTitle: String = "Invoice",
    val invoiceNumberLabel: String = "INVOICE #",
    val invoiceToLabel: String = "BILL TO"
) {
    companion object {
        val Empty = InvoiceInfo()
    }
}