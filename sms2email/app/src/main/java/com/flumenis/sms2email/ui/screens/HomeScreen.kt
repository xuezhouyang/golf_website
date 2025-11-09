package com.flumenis.sms2email.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flumenis.sms2email.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flumenis.sms2email.ui.MainViewModel
import com.flumenis.sms2email.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToTemplate: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToPremium: () -> Unit = {},
    onNavigateToCloudSync: () -> Unit = {},
    onNavigateToSmsForwarding: () -> Unit = {},
    onNavigateToTheme: () -> Unit = {},
    onNavigateToPermissions: () -> Unit = {},
    onNavigateToLogViewer: () -> Unit = {}
) {
    val emailConfig by viewModel.emailConfig.collectAsStateWithLifecycle(initialValue = null)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremiumActive.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportConfig(it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importConfig(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_postafide_logo_small),
                            contentDescription = "PostaFide Logo",
                            modifier = Modifier.size(28.dp),
                            tint = androidx.compose.ui.graphics.Color.Unspecified
                        )
                        Text("PostaFide")
                    }
                },
                actions = {
                    // Premium status bell icon with animation
                    PremiumBellIcon(
                        isPremium = isPremium,
                        onClick = onNavigateToPremium
                    )
                    IconButton(onClick = onNavigateToTheme) {
                        Icon(Icons.Default.Palette, contentDescription = "Theme")
                    }
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(Icons.Default.Info, contentDescription = "About")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Card
                StatusCard(
                    enabled = emailConfig?.enabled ?: false,
                    onToggle = { enabled ->
                        emailConfig?.let {
                            viewModel.saveEmailConfig(it.copy(enabled = enabled))
                        }
                    }
                )

                // Quick Actions
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Row 1: Basic actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        onClick = onNavigateToSettings,
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionCard(
                        icon = Icons.Default.Edit,
                        label = "Template",
                        onClick = onNavigateToTemplate,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Permissions & Logs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.Security,
                        label = "Permissions",
                        onClick = onNavigateToPermissions,
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionCard(
                        icon = Icons.Default.History,
                        label = "View Logs",
                        onClick = onNavigateToLogViewer,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Premium features
                Text(
                    text = "Premium Features",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.Cloud,
                        label = "Cloud Sync",
                        onClick = if (isPremium) onNavigateToCloudSync else onNavigateToPremium,
                        isPremium = true,
                        isLocked = !isPremium,
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionCard(
                        icon = Icons.Default.PhoneForwarded,
                        label = "SMS Forward",
                        onClick = if (isPremium) onNavigateToSmsForwarding else onNavigateToPremium,
                        isPremium = true,
                        isLocked = !isPremium,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Configuration Management
                Text(
                    text = "Configuration",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { exportLauncher.launch("sms2email_config.json") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export")
                    }

                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("application/json")) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Import")
                    }
                }

                // Configuration Summary
                emailConfig?.let { config ->
                    ConfigSummaryCard(config)
                }
            }

            // UI State Handling
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UiState.Success -> {
                    LaunchedEffect(state) {
                        // Show success snackbar
                        viewModel.clearUiState()
                    }
                }
                is UiState.Error -> {
                    LaunchedEffect(state) {
                        // Show error snackbar
                        viewModel.clearUiState()
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun StatusCard(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (enabled) "Service Active" else "Service Inactive",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = if (enabled) "SMS forwarding is enabled" else "SMS forwarding is disabled",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
fun ConfigSummaryCard(config: com.flumenis.sms2email.data.EmailConfig) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Current Configuration",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            SummaryItem("SMTP Server", config.smtpHost.ifBlank { "Not configured" })
            SummaryItem("From", config.fromEmail.ifBlank { "Not configured" })
            SummaryItem("To", config.toEmail.ifBlank { "Not configured" })
            SummaryItem(
                "Security",
                when {
                    config.smtpUseSsl -> "SSL"
                    config.smtpUseTls -> "TLS"
                    else -> "None"
                }
            )
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PremiumBellIcon(
    isPremium: Boolean,
    onClick: () -> Unit
) {
    // Animated bell icon
    val infiniteTransition = rememberInfiniteTransition(label = "bell")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    BadgedBox(
        badge = {
            if (!isPremium) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.error
                )
            } else {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    ) {
        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Premium Status",
                tint = if (isPremium) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPremium: Boolean = false,
    isLocked: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (isLocked) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    OutlinedCard(
        onClick = onClick,
        modifier = modifier.scale(scale),
        colors = if (isPremium) {
            CardDefaults.outlinedCardColors(
                containerColor = if (isLocked)
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            )
        } else {
            CardDefaults.outlinedCardColors()
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = if (isLocked)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        else if (isPremium)
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.primary
                    )

                    // Lock overlay for premium features
                    if (isLocked) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Locked",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(24.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isPremium) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isLocked)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                if (isPremium) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Stars,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = if (isLocked)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            else
                                MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Premium",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLocked)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            else
                                MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}
