package com.example.invoicemaker.data

/**
 * Domain-level Business Info used across the app (Business Info screen,
 * Invoice/Estimate Add & Edit screens, PDF generation).
 * Kept separate from BusinessInfoEntity so screens/ViewModels never
 * depend on Room directly.
 */
data class BusinessInfo(
    val businessName: String = "",
    val email: String = "",
    val phone: String = "",
    val addressLine1: String = "",
    val addressLine2: String = "",
    val website: String = "",
    val taxName: String = "",
    val taxId: String = "",
    val logoPath: String? = null
) {
    companion object {
        val Empty = BusinessInfo()
    }
}