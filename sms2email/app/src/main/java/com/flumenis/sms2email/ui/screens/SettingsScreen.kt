package com.flumenis.sms2email.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flumenis.sms2email.data.EmailConfig
import com.flumenis.sms2email.ui.MainViewModel
import com.flumenis.sms2email.ui.UiState
import com.flumenis.sms2email.util.ValidationUtils
import com.flumenis.sms2email.util.UserFeedback
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val emailConfig by viewModel.emailConfig.collectAsStateWithLifecycle(initialValue = null)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var smtpHost by remember { mutableStateOf("") }
    var smtpPort by remember { mutableStateOf("587") }
    var smtpUsername by remember { mutableStateOf("") }
    var smtpPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var smtpUseTls by remember { mutableStateOf(true) }
    var smtpUseSsl by remember { mutableStateOf(false) }
    var fromEmail by remember { mutableStateOf("") }
    var fromName by remember { mutableStateOf("SMS2Email") }
    var toEmail by remember { mutableStateOf("") }

    // Validation error states
    var smtpHostError by remember { mutableStateOf<String?>(null) }
    var smtpPortError by remember { mutableStateOf<String?>(null) }
    var fromEmailError by remember { mutableStateOf<String?>(null) }
    var toEmailError by remember { mutableStateOf<String?>(null) }

    // Load config when available
    LaunchedEffect(emailConfig) {
        emailConfig?.let { config ->
            smtpHost = config.smtpHost
            smtpPort = config.smtpPort.toString()
            smtpUsername = config.smtpUsername
            smtpPassword = config.smtpPassword
            smtpUseTls = config.smtpUseTls
            smtpUseSsl = config.smtpUseSsl
            fromEmail = config.fromEmail
            fromName = config.fromName
            toEmail = config.toEmail
        }
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                UserFeedback.showSuccess(scope, snackbarHostState, state.message)
                viewModel.clearUiState()
            }
            is UiState.Error -> {
                UserFeedback.showError(scope, snackbarHostState, state.message)
                viewModel.clearUiState()
            }
            else -> {}
        }
    }

    // Validation function
    fun validateInputs(): Boolean {
        var isValid = true

        // Validate SMTP host
        if (!ValidationUtils.isValidHost(smtpHost)) {
            smtpHostError = ValidationUtils.ErrorMessages.INVALID_HOST
            isValid = false
        } else {
            smtpHostError = null
        }

        // Validate SMTP port
        val port = smtpPort.toIntOrNull()
        if (port == null || !ValidationUtils.isValidPort(port)) {
            smtpPortError = ValidationUtils.ErrorMessages.INVALID_PORT
            isValid = false
        } else {
            smtpPortError = null
        }

        // Validate from email
        if (!ValidationUtils.isValidEmail(fromEmail)) {
            fromEmailError = ValidationUtils.ErrorMessages.INVALID_EMAIL
            isValid = false
        } else {
            fromEmailError = null
        }

        // Validate to email
        if (!ValidationUtils.isValidEmail(toEmail)) {
            toEmailError = ValidationUtils.ErrorMessages.INVALID_EMAIL
            isValid = false
        } else {
            toEmailError = null
        }

        return isValid
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Email Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "SMTP Server Configuration",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = smtpHost,
                    onValueChange = {
                        smtpHost = it
                        smtpHostError = null
                    },
                    label = { Text("SMTP Server") },
                    placeholder = { Text("smtp.gmail.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = smtpHostError != null,
                    supportingText = smtpHostError?.let { { Text(it) } }
                )

                OutlinedTextField(
                    value = smtpPort,
                    onValueChange = {
                        smtpPort = it
                        smtpPortError = null
                    },
                    label = { Text("SMTP Port") },
                    placeholder = { Text("587") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = smtpPortError != null,
                    supportingText = smtpPortError?.let { { Text(it) } }
                )

                OutlinedTextField(
                    value = smtpUsername,
                    onValueChange = { smtpUsername = it },
                    label = { Text("Username") },
                    placeholder = { Text("your@email.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = smtpPassword,
                    onValueChange = { smtpPassword = it },
                    label = { Text("Password") },
                    placeholder = { Text("Your password or app password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    singleLine = true
                )

                Text(
                    text = "Security",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Use TLS (Recommended)")
                    Switch(
                        checked = smtpUseTls,
                        onCheckedChange = {
                            smtpUseTls = it
                            if (it) smtpUseSsl = false
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Use SSL")
                    Switch(
                        checked = smtpUseSsl,
                        onCheckedChange = {
                            smtpUseSsl = it
                            if (it) smtpUseTls = false
                        }
                    )
                }

                Text(
                    text = "Email Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = fromEmail,
                    onValueChange = {
                        fromEmail = it
                        fromEmailError = null
                    },
                    label = { Text("From Email") },
                    placeholder = { Text("sender@example.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = fromEmailError != null,
                    supportingText = fromEmailError?.let { { Text(it) } }
                )

                OutlinedTextField(
                    value = fromName,
                    onValueChange = { fromName = it },
                    label = { Text("From Name") },
                    placeholder = { Text("SMS2Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = toEmail,
                    onValueChange = {
                        toEmail = it
                        toEmailError = null
                    },
                    label = { Text("To Email") },
                    placeholder = { Text("recipient@example.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = toEmailError != null,
                    supportingText = toEmailError?.let { { Text(it) } }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (validateInputs()) {
                                val config = EmailConfig(
                                    smtpHost = smtpHost,
                                    smtpPort = smtpPort.toIntOrNull() ?: 587,
                                    smtpUsername = smtpUsername,
                                    smtpPassword = smtpPassword,
                                    smtpUseTls = smtpUseTls,
                                    smtpUseSsl = smtpUseSsl,
                                    fromEmail = fromEmail,
                                    fromName = fromName,
                                    toEmail = toEmail,
                                    subjectTemplate = emailConfig?.subjectTemplate ?: "",
                                    bodyTemplate = emailConfig?.bodyTemplate ?: "",
                                    enabled = emailConfig?.enabled ?: false
                                )
                                viewModel.testConnection(config)
                            } else {
                                scope.launch {
                                    UserFeedback.showError(
                                        scope, snackbarHostState,
                                        "Please fix validation errors"
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test Connection")
                    }

                    Button(
                        onClick = {
                            if (validateInputs()) {
                                val config = EmailConfig(
                                    smtpHost = smtpHost,
                                    smtpPort = smtpPort.toIntOrNull() ?: 587,
                                    smtpUsername = smtpUsername,
                                    smtpPassword = smtpPassword,
                                    smtpUseTls = smtpUseTls,
                                    smtpUseSsl = smtpUseSsl,
                                    fromEmail = fromEmail,
                                    fromName = fromName,
                                    toEmail = toEmail,
                                    subjectTemplate = emailConfig?.subjectTemplate ?: "",
                                    bodyTemplate = emailConfig?.bodyTemplate ?: "",
                                    enabled = emailConfig?.enabled ?: false
                                )
                                viewModel.saveEmailConfig(config)
                                scope.launch {
                                    UserFeedback.showSuccess(
                                        scope, snackbarHostState,
                                        "Settings saved successfully"
                                    )
                                }
                                onNavigateBack()
                            } else {
                                scope.launch {
                                    UserFeedback.showError(
                                        scope, snackbarHostState,
                                        "Please fix validation errors"
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
