package com.flumenis.sms2email.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flumenis.sms2email.util.PermissionManager

/**
 * Permission Settings Screen
 *
 * Displays all app permissions with:
 * - Current status (granted/denied)
 * - Description and reasoning
 * - Instructions for granting
 * - Link to app settings (no direct permission requests)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val permissionManager = remember { PermissionManager(context) }

    var permissionStatuses by remember {
        mutableStateOf(permissionManager.getAllPermissionStatuses())
    }
    var selectedPermission by remember { mutableStateOf<PermissionManager.PermissionStatus?>(null) }
    var showInstructionsDialog by remember { mutableStateOf(false) }

    // Refresh permission statuses when returning to screen
    DisposableEffect(Unit) {
        val listener = {
            permissionStatuses = permissionManager.getAllPermissionStatuses()
        }
        onDispose { }
    }

    // Refresh on resume
    LaunchedEffect(Unit) {
        permissionStatuses = permissionManager.getAllPermissionStatuses()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Permissions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            permissionStatuses = permissionManager.getAllPermissionStatuses()
                        }
                    ) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Permission summary card
            val summary = permissionManager.getPermissionSummary()
            PermissionSummaryCard(summary, permissionManager, context)

            Spacer(modifier = Modifier.height(16.dp))

            // Required permissions section
            Text(
                text = "Required Permissions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            permissionStatuses.filter { it.isRequired }.forEach { status ->
                PermissionCard(
                    status = status,
                    onClick = {
                        selectedPermission = status
                        showInstructionsDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Optional permissions section
            Text(
                text = "Optional Permissions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = "These permissions enhance app functionality",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            permissionStatuses.filter { !it.isRequired }.forEach { status ->
                PermissionCard(
                    status = status,
                    onClick = {
                        selectedPermission = status
                        showInstructionsDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Instructions dialog
    if (showInstructionsDialog && selectedPermission != null) {
        PermissionInstructionsDialog(
            permission = selectedPermission!!,
            permissionManager = permissionManager,
            onDismiss = {
                showInstructionsDialog = false
                // Refresh statuses after user returns
                permissionStatuses = permissionManager.getAllPermissionStatuses()
            }
        )
    }
}

@Composable
fun PermissionSummaryCard(
    summary: PermissionManager.PermissionSummary,
    permissionManager: PermissionManager,
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (summary.hasCorePermissions) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (summary.hasCorePermissions) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Warning
                    },
                    contentDescription = null,
                    tint = if (summary.hasCorePermissions) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    },
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (summary.hasCorePermissions) {
                            "Core Permissions Granted"
                        } else {
                            "Missing Required Permissions"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${summary.grantedPermissions}/${summary.totalPermissions} permissions granted",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (!summary.hasCorePermissions) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "The app requires ${summary.missingCore.size} core permission(s) to function properly.",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        context.startActivity(permissionManager.openAppSettings())
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Settings, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open App Settings")
                }
            }
        }
    }
}

@Composable
fun PermissionCard(
    status: PermissionManager.PermissionStatus,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status icon
            Icon(
                imageVector = if (status.isGranted) {
                    Icons.Default.CheckCircle
                } else {
                    Icons.Default.Cancel
                },
                contentDescription = if (status.isGranted) "Granted" else "Denied",
                tint = if (status.isGranted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Permission info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = status.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (status.isRequired) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge {
                            Text("Required", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = status.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow icon
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PermissionInstructionsDialog(
    permission: PermissionManager.PermissionStatus,
    permissionManager: PermissionManager,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = if (permission.isGranted) {
                    Icons.Default.CheckCircle
                } else {
                    Icons.Default.Info
                },
                contentDescription = null,
                tint = if (permission.isGranted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        },
        title = {
            Text(
                text = permission.displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Status: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (permission.isGranted) "Granted ✓" else "Not Granted ✗",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (permission.isGranted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = "What it does:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = permission.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Reasoning
                Text(
                    text = "Why we need it:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = permission.reasoning,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!permission.isGranted) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Divider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Instructions
                    Text(
                        text = "How to grant:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = permissionManager.getPermissionInstructions(permission.permission),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            if (!permission.isGranted) {
                Button(
                    onClick = {
                        context.startActivity(permissionManager.openAppSettings())
                        onDismiss()
                    }
                ) {
                    Icon(Icons.Default.Settings, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Settings")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        },
        dismissButton = {
            if (!permission.isGranted) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
