package com.flumenis.sms2email.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flumenis.sms2email.ui.MainViewModel
import com.flumenis.sms2email.ui.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var activationCode by remember { mutableStateOf("") }
    var verificationResult by remember { mutableStateOf<String?>(null) }
    var isLockedOut by remember { mutableStateOf(false) }
    var lockoutTimeRemaining by remember { mutableStateOf("") }
    var showDeviceInfo by remember { mutableStateOf(false) }

    // Collect states from ViewModel
    val isPremium by viewModel.isPremiumActive.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val attemptsRemaining by remember {
        derivedStateOf { viewModel.getAttemptsRemaining() }
    }

    // Get device info
    val deviceInfo = remember { viewModel.getDeviceInfo() }

    // Check activation status on launch
    LaunchedEffect(Unit) {
        isLockedOut = viewModel.isLockedOut()
        lockoutTimeRemaining = viewModel.getRemainingLockoutTime()
    }

    // Update lockout countdown every minute
    LaunchedEffect(isLockedOut) {
        if (isLockedOut) {
            while (viewModel.isLockedOut()) {
                kotlinx.coroutines.delay(60000) // Update every minute
                isLockedOut = viewModel.isLockedOut()
                lockoutTimeRemaining = viewModel.getRemainingLockoutTime()
            }
            // Lockout expired, update state
            isLockedOut = false
        }
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                verificationResult = state.message
                inviteCode = ""
                viewModel.clearUiState()
            }
            is UiState.Error -> {
                verificationResult = state.message
                viewModel.clearUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Premium Subscription") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Premium status card with animation
            AnimatedVisibility(
                visible = isPremium,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                PremiumStatusCard(
                    isPremium = true,
                    activatedCode = viewModel.getActivatedCode() ?: "UNKNOWN"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Premium features list
            if (!isPremium) {
                PremiumFeaturesCard()
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Invite code input section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (isPremium) "Change Activation Code" else "Enter Activation Code",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Please contact the author to obtain an activation code",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = activationCode,
                        onValueChange = {
                            activationCode = it.uppercase().replace(" ", "")
                            verificationResult = null
                        },
                        label = { Text("Activation Code") },
                        placeholder = { Text("XXXXX-XXXXX-XXXXX-XXXXX-XXXXX") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLockedOut && attemptsRemaining > 0,
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Key, "Activation")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lockout warning
                    if (isLockedOut) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Account Locked",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "Too many failed attempts. Try again in: $lockoutTimeRemaining",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    } else if (attemptsRemaining < 10) {
                        // Attempts remaining warning
                        Text(
                            text = "Attempts remaining: $attemptsRemaining/10",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (attemptsRemaining <= 3)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Verify button with loading state
                    val isLoading = uiState is UiState.Loading
                    Button(
                        onClick = {
                            verificationResult = null
                            viewModel.activateWithCode(activationCode)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = activationCode.isNotBlank() && !isLoading && !isLockedOut && attemptsRemaining > 0
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verifying...")
                        } else {
                            Icon(Icons.Default.CheckCircle, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Activate Premium")
                        }
                    }

                    // Verification result
                    verificationResult?.let { result ->
                        Spacer(modifier = Modifier.height(8.dp))
                        AnimatedContent(
                            targetState = result,
                            transitionSpec = {
                                slideInVertically { it } + fadeIn() togetherWith
                                slideOutVertically { -it } + fadeOut()
                            },
                            label = "result"
                        ) { text ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (text.startsWith("Success"))
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (text.startsWith("Success"))
                                            Icons.Default.CheckCircle
                                        else
                                            Icons.Default.Error,
                                        contentDescription = null,
                                        tint = if (text.startsWith("Success"))
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (text.startsWith("Success"))
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (attemptsRemaining == 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Too Many Attempts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Please try again in 24 hours",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumStatusCard(isPremium: Boolean, activatedCode: String? = null) {
    val infiniteTransition = rememberInfiniteTransition(label = "premium")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Stars,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Premium Activated",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (activatedCode != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Code: $activatedCode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumFeaturesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Premium Features",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            PremiumFeatureItem(
                icon = Icons.Default.Cloud,
                title = "Cloud Sync",
                description = "Backup to Google Drive, OneDrive, or GitHub"
            )
            PremiumFeatureItem(
                icon = Icons.Default.PhoneAndroid,
                title = "SMS Forwarding",
                description = "Forward SMS to phone numbers with dual SIM support"
            )
            PremiumFeatureItem(
                icon = Icons.Default.History,
                title = "Version History",
                description = "Access configuration history and rollback"
            )
            PremiumFeatureItem(
                icon = Icons.Default.Security,
                title = "Enhanced Security",
                description = "Device fingerprint binding and encryption"
            )
        }
    }
}

@Composable
fun PremiumFeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
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
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
