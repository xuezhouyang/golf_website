package com.flumenis.sms2email.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flumenis.sms2email.data.EmailConfig
import com.flumenis.sms2email.data.TemplateVariables
import com.flumenis.sms2email.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val emailConfig by viewModel.emailConfig.collectAsStateWithLifecycle(initialValue = null)
    val clipboardManager = LocalClipboardManager.current

    var subjectTemplate by remember { mutableStateOf("") }
    var bodyTemplate by remember { mutableStateOf("") }

    // Load config when available
    LaunchedEffect(emailConfig) {
        emailConfig?.let { config ->
            subjectTemplate = config.subjectTemplate
            bodyTemplate = config.bodyTemplate
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Email Template") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Available Variables",
                style = MaterialTheme.typography.titleMedium
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TemplateVariables.ALL_VARIABLES.forEach { (variable, description) ->
                        VariableItem(
                            variable = variable,
                            description = description,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(variable))
                            }
                        )
                        if (variable != TemplateVariables.ALL_VARIABLES.last().first) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }

                    // Additional variables
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Enhanced Variables:",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    listOf(
                        "{{Text}}" to "Message text",
                        "{{ContactName}}" to "Contact name from address book",
                        "{{FromNumber}}" to "Sender phone number",
                        "{{ToNumber}}" to "Device phone number",
                        "{{OccurredAt}}" to "Full timestamp",
                        "{{OsName}}" to "Operating system (Android version)",
                        "{{DeviceName}}" to "Device model and manufacturer"
                    ).forEach { (variable, description) ->
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        VariableItem(
                            variable = variable,
                            description = description,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(variable))
                            }
                        )
                    }
                }
            }

            Text(
                text = "HTML Support",
                style = MaterialTheme.typography.titleMedium
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "You can use HTML tags in templates:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "• <b>text</b> - Bold\n• <br> - Line break\n• <i>text</i> - Italic",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text(
                text = "Subject Template",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = subjectTemplate,
                onValueChange = { subjectTemplate = it },
                label = { Text("Subject") },
                placeholder = { Text("SMS from {{sender}} ({{sim_slot}})") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 1,
                maxLines = 3
            )

            Text(
                text = "Body Template",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = bodyTemplate,
                onValueChange = { bodyTemplate = it },
                label = { Text("Email Body") },
                placeholder = {
                    Text(
                        """<b>{{Text}}</b><br>
From {{ContactName}} {{FromNumber}}<br>
To {{ToNumber}}<br>
{{OccurredAt}}<br>
via {{OsName}} {{DeviceName}}"""
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                minLines = 8
            )

            Button(
                onClick = {
                    emailConfig?.let { config ->
                        viewModel.saveEmailConfig(
                            config.copy(
                                subjectTemplate = subjectTemplate,
                                bodyTemplate = bodyTemplate
                            )
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Template")
            }
        }
    }
}

@Composable
fun VariableItem(
    variable: String,
    description: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCopy)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = variable,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onCopy) {
            Icon(
                Icons.Default.ContentCopy,
                contentDescription = "Copy",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
