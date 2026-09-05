package com.example.invoicemaker.ui.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// MoreScreen
// ---------------------------------------------------------------------------

@Composable
fun MoreScreen(
    onNewBusinessClick: () -> Unit = {},
    onDashboardClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onDeliveryNotesClick: () -> Unit = {},
    onExpensesClick: () -> Unit = {},
    onArchivedClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onBackupRestoreClick: () -> Unit = {},
    onShareAppClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onRateUsClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Title
        Text(
            text = "More",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 16.dp)
        )

        // Group 1 — New Business (special row with subtitle)
        MoreSection {
            NewBusinessItem(onClick = onNewBusinessClick)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group 2 — Dashboard / Report
        MoreSection {
            MoreMenuItem(
                icon = Icons.Filled.Dashboard,
                title = "Dashboard",
                onClick = onDashboardClick
            )
            MoreDivider()
            MoreMenuItem(
                icon = Icons.Filled.Assessment,
                title = "Report",
                onClick = onReportClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group 3 — Delivery Notes / Expenses
        MoreSection {
            MoreMenuItem(
                icon = Icons.Filled.LocalShipping,
                title = "Delivery Notes",
                onClick = onDeliveryNotesClick
            )
            MoreDivider()
            MoreMenuItem(
                icon = Icons.Filled.Receipt,
                title = "Expenses",
                onClick = onExpensesClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group 4 — Archived / Export
        MoreSection {
            MoreMenuItem(
                icon = Icons.Filled.Archive,
                title = "Archived",
                onClick = onArchivedClick
            )
            MoreDivider()
            MoreMenuItem(
                icon = Icons.Filled.FileDownload,
                title = "Export",
                onClick = onExportClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group 5 — Settings / Backup & Restore (with helper subtitle)
        MoreSection {
            MoreMenuItem(
                icon = Icons.Filled.Settings,
                title = "Settings",
                onClick = onSettingsClick
            )
            MoreDivider()
            MoreMenuItem(
                icon = Icons.Filled.Backup,
                title = "Backup & Restore",
                subtitle = "Backup your data regularly as a safety measure",
                onClick = onBackupRestoreClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group 6 — Share / Support / Rate us
        MoreSection {
            MoreMenuItem(
                icon = Icons.Filled.Share,
                title = "Share this app",
                onClick = onShareAppClick
            )
            MoreDivider()
            MoreMenuItem(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                title = "Support",
                onClick = onSupportClick
            )
            MoreDivider()
            MoreMenuItem(
                icon = Icons.Filled.Star,
                title = "Rate us",
                onClick = onRateUsClick
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ---------------------------------------------------------------------------
// Reusable pieces
// ---------------------------------------------------------------------------

/** Rounded card wrapper that groups a set of MoreMenuItem rows together. */
@Composable
private fun MoreSection(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        content = content
    )
}

/** Thin divider used between rows inside a MoreSection. */
@Composable
private fun MoreDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

/** Standard row: icon — title (+ optional subtitle) — arrow. */
@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Special first row: icon + "New Business" title on top line, subtitle below, arrow at the right (vertically centered on the whole row). */
@Composable
private fun NewBusinessItem(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Business,
                    contentDescription = "New Business",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(18.dp))
                Text(
                    text = "New Business",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "Add Your Business Details",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 42.dp, top = 4.dp)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
private fun MoreScreenPreview() {
    MaterialTheme {
        MoreScreen()
    }
}