package com.rubecoder.rubeaurachecker.presentation.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rubecoder.rubeaurachecker.R
import com.rubecoder.rubeaurachecker.core.theme.NeonRed
import com.rubecoder.rubeaurachecker.core.theme.NeonWhite
import com.rubecoder.rubeaurachecker.core.theme.PureBlack
import com.rubecoder.rubeaurachecker.core.theme.RubeAuraTheme
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(navigateBack: () -> Unit) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                navigateBack = navigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PureBlack)
        ) {
            SettingsHeader()
            Spacer(Modifier.height(24.dp))
            SettingsPreferences(
                enableHaptics = uiState.enableHaptics,
                onEnableHapticsChanged = { viewModel.onEnableHapticsChanged(it) },
                enableSound = uiState.enableSound,
                onEnableSoundChanged = { viewModel.onEnableSoundChanged(it) },
                enableAnimations = uiState.enableAnimations,
                onEnableAnimationsChanged = { viewModel.onEnableAnimationsChanged(it) }
            )
            Spacer(Modifier.height(32.dp))
            SettingsActions(
                onResetPreferences = { viewModel.onResetPreferences() }
            )
        }
    }
}

@Composable
fun SettingsTopAppBar(
    navigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.settings_title),
                color = NeonRed,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = NeonRed
                )
            }
        },
        backgroundColor = PureBlack,
        containerColor = PureBlack
    )
}

@Composable
fun SettingsHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = NeonRed,
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.settings_title),
            color = NeonRed,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(R.string.settings_subtitle),
            color = MaterialTheme.typography.bodyLarge.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
fun SettingsPreferences(
    enableHaptics: Boolean,
    onEnableHapticsChanged: (Boolean) -> Unit,
    enableSound: Boolean,
    onEnableSoundChanged: (Boolean) -> Unit,
    enableAnimations: Boolean,
    onEnableAnimationsChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SettingsToggleItem(
            title = stringResource(R.string.settings_enable_haptics),
            description = stringResource(R.string.settings_enable_haptics_desc),
            isChecked = enableHaptics,
            onCheckedChange = onEnableHapticsChanged
        )

        SettingsToggleItem(
            title = stringResource(R.string.settings_enable_sound),
            description = stringResource(R.string.settings_enable_sound_desc),
            isChecked = enableSound,
            onCheckedChange = onEnableSoundChanged
        )

        SettingsToggleItem(
            title = stringResource(R.string.settings_enable_animations),
            description = stringResource(R.string.settings_enable_animations_desc),
            isChecked = enableAnimations,
            onCheckedChange = onEnableAnimationsChanged
        )

        // Accessibility options
        SettingsSectionHeader(
            title = stringResource(R.string.settings_accessibility)
        )

        SettingsToggleItem(
            title = stringResource(R.string.settings_reduce_motion),
            description = stringResource(R.string.settings_reduce_motion_desc),
            isChecked = false, // This would be tied to system settings in a real app
            onCheckedChange = { /* TODO: Implement reduce motion */ }
        )

        SettingsToggleItem(
            title = stringResource(R.string.settings_screen_reader_support),
            description = stringResource(R.string.settings_screen_reader_desc),
            isChecked = false, // This would be tied to system settings in a real app
            onCheckedChange = { /* TODO: Implement screen reader enhancements */ }
        )
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Switch
        Switch(
            checked = isChecked,
            onCheckedChange = { onCheckedChange(it) },
            colors = SwitchDefaults.switchColors(
                thumbColor = { if (it) NeonRed else MaterialTheme.colorScheme.onSurfaceVariant },
                trackColor = { if (it) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f) }
            )
        )
    }
}

@Composable
fun SettingsSectionHeader(
    title: String
) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 24.dp)
            .padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsActions(
    onResetPreferences: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = onResetPreferences,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_reset_preferences),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Version info
        Text(
            text = stringResource(R.string.settings_version_info),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
    }
}