package com.flumenis.sms2email.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flumenis.sms2email.sync.CloudProvider
import com.flumenis.sms2email.ui.MainViewModel
import com.flumenis.sms2email.ui.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudSyncScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var selectedProvider by remember { mutableStateOf<CloudProvider?>(null) }
    var syncResult by remember { mutableStateOf<String?>(null) }
    var lastSyncTime by remember { mutableStateOf<String?>(null) }
    var accessToken by remember { mutableStateOf("") }
    var showTokenDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Collect states from ViewModel
    val isPremium by viewModel.isPremiumActive.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                syncResult = state.message
                lastSyncTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                viewModel.clearUiState()
            }
            is UiState.Error -> {
                syncResult = state.message
                viewModel.clearUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cloud Sync") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Premium lock banner
            if (!isPremium) {
                PremiumRequiredBanner()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Last sync info
            lastSyncTime?.let { time ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Last Sync",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = time,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Cloud provider selection
            Text(
                text = "Choose Cloud Provider",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Google Drive
            CloudProviderCard(
                provider = CloudProvider.GOOGLE_DRIVE,
                icon = Icons.Default.Cloud,
                title = "Google Drive",
                description = "Sync to Google Drive with OAuth 2.0",
                isSelected = selectedProvider == CloudProvider.GOOGLE_DRIVE,
                isEnabled = isPremium && !(uiState is UiState.Loading),
                onClick = { selectedProvider = CloudProvider.GOOGLE_DRIVE }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // OneDrive
            CloudProviderCard(
                provider = CloudProvider.ONEDRIVE,
                icon = Icons.Default.CloudUpload,
                title = "OneDrive",
                description = "Microsoft OneDrive via Graph API",
                isSelected = selectedProvider == CloudProvider.ONEDRIVE,
                isEnabled = isPremium && !(uiState is UiState.Loading),
                onClick = { selectedProvider = CloudProvider.ONEDRIVE }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // GitHub Gist
            CloudProviderCard(
                provider = CloudProvider.GITHUB,
                icon = Icons.Default.Code,
                title = "GitHub Gist",
                description = "Private Gist for configuration backup",
                isSelected = selectedProvider == CloudProvider.GITHUB,
                isEnabled = isPremium && !(uiState is UiState.Loading),
                onClick = { selectedProvider = CloudProvider.GITHUB }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sync actions
            val isLoading = uiState is UiState.Loading
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Backup button
                Button(
                    onClick = {
                        if (selectedProvider != null) {
                            syncResult = null
                            // Using demo token for now - in production, implement OAuth flow
                            viewModel.backupToCloud(selectedProvider!!, accessToken.ifBlank { "demo_token" })
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedProvider != null && !isLoading && isPremium
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.CloudUpload, null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isLoading) "Syncing..." else "Backup")
                }

                // Restore button
                OutlinedButton(
                    onClick = {
                        if (selectedProvider != null) {
                            syncResult = null
                            // Using demo token for now - in production, implement OAuth flow
                            viewModel.restoreFromCloud(selectedProvider!!, accessToken.ifBlank { "demo_token" })
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedProvider != null && !isLoading && isPremium
                ) {
                    Icon(Icons.Default.CloudDownload, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restore")
                }
            }

            // Sync result
            syncResult?.let { result ->
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(
                    visible = true,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = result,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Help text
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "How it works",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Select a cloud provider\n" +
                                "• Tap 'Backup' to save your config\n" +
                                "• Tap 'Restore' to load saved config\n" +
                                "• Requires Premium subscription",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CloudProviderCard(
    provider: CloudProvider,
    icon: ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (isEnabled) onClick else ({}),
        enabled = isEnabled,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            CardDefaults.outlinedCardBorder().copy(width = 2.dp)
        else
            null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun PremiumRequiredBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Stars,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Premium Feature",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Cloud sync requires Premium subscription",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            IconButton(onClick = { /* Navigate to premium */ }) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Upgrade",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}
