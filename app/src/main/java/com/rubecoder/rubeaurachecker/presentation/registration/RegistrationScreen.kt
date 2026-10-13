package com.rubecoder.rubeaurachecker.presentation.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardActions
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.rubecoder.rubeaurachecker.R
import com.rubecoder.rubeaurachecker.core.theme.RubeAuraTheme
import com.rubecoder.rubeaurachecker.core.theme.NeonWhite
import com.rubecoder.rubeaurachecker.core.ui.RubeAuraButton
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = hiltViewModel(),
    onRegistrationComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate once registration completes (either from a fresh submit or because
    // a previously-finished registration was loaded from storage).
    LaunchedEffect(uiState.isRegistrationComplete) {
        if (uiState.isRegistrationComplete) onRegistrationComplete()
    }

    RubeAuraTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // App name/logo
            Text(
                text = stringResource(R.string.app_name),
                color = NeonWhite,
                fontSize = 32.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 32.dp)
            )

            // Full Name Field
            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = { viewModel.onFullNameChanged(it) },
                label = { Text(stringResource(R.string.full_name)) },
                placeholder = { Text(stringResource(R.string.placeholder_full_name)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // School Name Field
            OutlinedTextField(
                value = uiState.schoolName,
                onValueChange = { viewModel.onSchoolNameChanged(it) },
                label = { Text(stringResource(R.string.school_name)) },
                placeholder = { Text(stringResource(R.string.placeholder_school_name)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Roll Number Field
            OutlinedTextField(
                value = uiState.rollNumber,
                onValueChange = { viewModel.onRollNumberChanged(it) },
                label = { Text(stringResource(R.string.roll_number)) },
                placeholder = { Text(stringResource(R.string.placeholder_roll_number)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Consent Disclosure
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.consentGiven,
                    onCheckedChange = { viewModel.onConsentChanged(it) },
                    modifier = Modifier.size(24.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFFF1744), // Neon red
                        uncheckedColor = Color(0x40FFFF0000), // Neon red faint
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.consent_disclosure),
                    color = NeonWhite,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            // Error Message
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFFF5252), // Red error
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .wrapContentWidth(align = androidx.compose.ui.Alignment.Start)
                )
            }

            // Register Button
            if (uiState.isLoading) {
                // Show loading indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = NeonWhite,
                        strokeWidth = 2.dp
                    )
                }
            } else {
                RubeAuraButton(
                    text = stringResource(R.string.register),
                    enabled = !uiState.isLoading,
                    onClick = { viewModel.onRegisterClicked() }
                )
            }

            // Optional: Text link to skip registration (for development/testing)
            Text(
                text = stringResource(R.string.skip_registration),
                color = Color(0x40FFFF0000), // Neon red faint
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(androidx.compose.ui.Alignment.CenterEnd)
                    .clickable {
                        // For development only: skip registration and go to home
                        onRegistrationComplete()
                    }
            )
        }
    }
}