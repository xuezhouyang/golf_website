package com.flumenis.sms2email.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flumenis.sms2email.ui.MainViewModel
import kotlinx.coroutines.launch

// SIM slot data model
data class SimSlotData(
    val slotIndex: Int,
    val carrierName: String,
    val displayName: String,
    val phoneNumber: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsForwardingScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("sms_forwarding", android.content.Context.MODE_PRIVATE) }

    var forwardingEnabled by remember { mutableStateOf(prefs.getBoolean("enabled", false)) }
    var targetNumber by remember { mutableStateOf(prefs.getString("target_number", "") ?: "") }
    var selectedSimSlot by remember { mutableStateOf(prefs.getInt("sim_slot", -1)) }
    var availableSimSlots by remember { mutableStateOf<List<SimSlotData>>(emptyList()) }

    // Collect premium status from ViewModel
    val isPremium by viewModel.isPremiumActive.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        availableSimSlots = listOf(
            SimSlotData(0, "China Mobile", "SIM 1", "+86 138****1234"),
            SimSlotData(1, "China Unicom", "SIM 2", "+86 186****5678")
        )
    }

    // Save configuration when changed
    fun saveConfig() {
        prefs.edit()
            .putBoolean("enabled", forwardingEnabled)
            .putString("target_number", targetNumber)
            .putInt("sim_slot", selectedSimSlot)
            .apply()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SMS Forwarding") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
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

            // Enable forwarding switch
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (forwardingEnabled)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (forwardingEnabled) Icons.Default.PhoneForwarded else Icons.Default.PhonePaused,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = if (forwardingEnabled)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SMS Forwarding",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (forwardingEnabled) "Active" else "Inactive",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = forwardingEnabled,
                        onCheckedChange = {
                            forwardingEnabled = it && isPremium
                            saveConfig()
                        },
                        enabled = isPremium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target number input
            AnimatedVisibility(
                visible = forwardingEnabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Text(
                        text = "Forward To",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = targetNumber,
                        onValueChange = { targetNumber = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("+86 138 0013 8000") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, "Phone")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SIM slot selection
            Text(
                text = "Select SIM Slot for Sending",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Auto (Default) option
            SimSlotSelectionCard(
                title = "Auto (Default)",
                description = "Use default SIM card",
                icon = Icons.Default.AutoAwesome,
                isSelected = selectedSimSlot == -1,
                isEnabled = isPremium,
                onClick = { selectedSimSlot = -1 }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Available SIM slots
            for (simSlot in availableSimSlots) {
                Spacer(modifier = Modifier.height(8.dp))
                SimSlotSelectionCard(
                    title = simSlot.displayName,
                    description = "${simSlot.carrierName} • ${simSlot.phoneNumber}",
                    icon = Icons.Default.SimCard,
                    isSelected = selectedSimSlot == simSlot.slotIndex,
                    isEnabled = isPremium,
                    onClick = { selectedSimSlot = simSlot.slotIndex }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    saveConfig()
                    // Show confirmation
                    scope.launch {
                        android.widget.Toast.makeText(
                            context,
                            "SMS forwarding configuration saved",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = forwardingEnabled && targetNumber.isNotBlank() && isPremium
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Configuration")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Help info
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
                        text = "• Incoming SMS will be forwarded to specified number\n" +
                                "• Choose which SIM slot to use for forwarding\n" +
                                "• Long messages are automatically split\n" +
                                "• Delivery tracking is enabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Permissions info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Required Permissions",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SEND_SMS permission is required for forwarding",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimSlotSelectionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
                modifier = Modifier.size(40.dp),
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
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
