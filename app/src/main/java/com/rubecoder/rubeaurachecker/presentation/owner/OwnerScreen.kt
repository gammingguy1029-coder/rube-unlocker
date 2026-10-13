package com.rubecoder.rubeaurachecker.presentation.owner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.rubecoder.rubeaurachecker.R
import com.rubecoder.rubeaurachecker.core.theme.RubeAuraTheme
import com.rubecoder.rubeaurachecker.core.theme.NeonRed
import com.rubecoder.rubeaurachecker.core.theme.NeonWhite
import com.rubecoder.rubeaurachecker.core.ui.RubeAuraButton
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.CircularProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerScreen(
    viewModel: OwnerViewModel = hiltViewModel(),
    onOwnerAuthenticated: (String) -> Unit = { }
) {
    val uiState by viewModel.uiState.collectAsState()

    var pin by remember { mutableStateOf("") }

    RubeAuraTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.owner_access),
                style = MaterialTheme.typography.headlineLarge,
                color = NeonWhite,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.enter_pin),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // PIN input field
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it.filter(Char::isDigit).take(4) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                label = { Text(stringResource(R.string.pin)) },
                supportingText = {
                    Text(
                        text = stringResource(R.string.pin_hint),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                isError = uiState.error != null,
                enabled = !uiState.isThrottled && !uiState.isSubmitting,
                trailingIcon = {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = NeonRed
                        )
                    }
                }
            )

            // Error message
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Failed attempts counter
            if (uiState.failedAttempts > 0 && !uiState.isThrottled) {
                Text(
                    text = stringResource(R.string.attempts_remaining, 3 - uiState.failedAttempts),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Unlock button
            RubeAuraButton(
                text = stringResource(R.string.unlock),
                onClick = {
                    viewModel.onPinEntered(pin)
                    pin = ""
                },
                enabled = !uiState.isThrottled && !uiState.isSubmitting && pin.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Warning about client-side PIN
            Text(
                text = stringResource(R.string.pin_warning),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )

            // If authenticated, navigate to the owner dashboard with the admin token
            if (uiState.isOwnerAuthenticated) {
                LaunchedEffect(uiState.isOwnerAuthenticated) {
                    onOwnerAuthenticated(uiState.adminToken?.token ?: "")
                }
            }
        }
    }
}