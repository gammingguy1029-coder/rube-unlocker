package com.rubecoder.rubeaurachecker.presentation.ownermode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.ArrowDropDown
import androidx.compose.material3.icons.filled.ArrowDropUp
import androidx.compose.material3.icons.filled.Delete
import androidx.compose.material3.icons.filled.ExitToApp
import androidx.compose.material3.icons.filled.Lock
import androidx.compose.material3.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rubecoder.rubeaurachecker.R
import com.rubecoder.rubeaurachecker.core.theme.NeonRed
import com.rubecoder.rubeaurachecker.core.theme.PureBlack
import com.rubecoder.rubeaurachecker.domain.model.AdminRegistration
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OwnerModeScreen(
    navigateBack: () -> Unit,
    initialToken: String? = null
) {
    val viewModel: OwnerModeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val registrations by viewModel.registrations.collectAsState()
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSchool by viewModel.selectedSchool.collectAsState()

    // If an initial token was passed from navigation, set it immediately.
    LaunchedEffect(initialToken) {
        initialToken?.takeIf { it.isNotBlank() }?.let { viewModel.onTokenReceived(it) }
    }

    // UI state
    val pinInput by remember { mutableStateOf("") }
    val expandedSchool by remember { mutableStateOf(false) }
    val schools = listOf("All Schools", "Lincoln High", "Washington Academy", "Jefferson Prep", "Roosevelt Charter")

    Scaffold(
        topBar = {
            OwnerModeTopAppBar(
                navigateBack = navigateBack,
                onSignOut = { viewModel.onSignOut() },
                isAuthenticated = uiState.isAuthenticated
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PureBlack)
        ) {
            if (!uiState.isAuthenticated) {
                OwnerModeAuthView(
                    pinInput = pinInput,
                    isAuthenticating = isAuthenticating,
                    onPinChanged = { pinInput = it },
                    onPinEntered = { viewModel.onPinEntered(it) },
                    errorMessage = uiState.errorMessage
                )
            } else {
                OwnerModeContentView(
                    registrations = registrations,
                    searchQuery = searchQuery,
                    selectedSchool = selectedSchool,
                    expandedSchool = expandedSchool,
                    onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                    onSchoolSelected = { viewModel.onSchoolSelected(it) },
                    onExpandedSchoolChanged = { expandedSchool = it },
                    onDeleteRegistration = { viewModel.onDeleteRegistration(it) },
                    schools = schools
                )
            }
        }
    }
}

@Composable
fun OwnerModeTopAppBar(
    navigateBack: () -> Unit,
    onSignOut: () -> Unit,
    isAuthenticated: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.owner_mode_title),
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
        actions = {
            if (isAuthenticated) {
                IconButton(onClick = onSignOut) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = NeonRed
                    )
                }
            }
        },
        backgroundColor = PureBlack,
        containerColor = PureBlack
    )
}

@Composable
fun OwnerModeAuthView(
    pinInput: String,
    isAuthenticating: Boolean,
    onPinChanged: (String) -> Unit,
    onPinEntered: (String) -> Unit,
    errorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = NeonRed,
            modifier = Modifier.size(80.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = stringResource(R.string.enter_owner_pin),
            color = NeonRed,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(R.string.owner_pin_hint),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = pinInput,
            onValueChange = onPinChanged,
            label = { Text(stringResource(R.string.pin_label)) },
            placeholder = { Text("••••") },
            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            ),
            maxLength = 4,
            isError = errorMessage != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .padding(bottom = 24.dp)
            )
        }

        Button(
            onClick = {
                if (pinInput.isNotEmpty()) {
                    onPinEntered(pinInput)
                }
            },
            enabled = pinInput.length == 4,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isAuthenticating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.authenticate),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun OwnerModeContentView(
    registrations: List<AdminRegistration>,
    searchQuery: String,
    selectedSchool: String,
    expandedSchool: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onSchoolSelected: (String) -> Unit,
    onExpandedSchoolChanged: (Boolean) -> Unit,
    onDeleteRegistration: (String) -> Unit,
    schools: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        OwnerModeSearchFilterBar(
            searchQuery = searchQuery,
            selectedSchool = selectedSchool,
            expandedSchool = expandedSchool,
            onSearchQueryChanged = onSearchQueryChanged,
            onSchoolSelected = onSchoolSelected,
            onExpandedSchoolChanged = onExpandedSchoolChanged,
            schools = schools
        )

        if (registrations.isEmpty()) {
            OwnerModeEmptyState()
        } else {
            OwnerModeRegistrationsList(
                registrations = registrations,
                onDeleteRegistration = onDeleteRegistration
            )
        }
    }
}

@Composable
fun OwnerModeSearchFilterBar(
    searchQuery: String,
    selectedSchool: String,
    expandedSchool: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onSchoolSelected: (String) -> Unit,
    onExpandedSchoolChanged: (Boolean) -> Unit,
    schools: List<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp))
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            label = { Text(stringResource(R.string.search_registrations)) },
            placeholder = { Text(stringResource(R.string.search_by_name_or_school)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            ),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        Column(
            modifier = Modifier
                .weight(0.3f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.filter_by_school),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            ExpandableSchoolDropdown(
                selectedSchool = selectedSchool,
                expandedSchool = expandedSchool,
                onSchoolSelected = onSchoolSelected,
                onExpandedSchoolChanged = onExpandedSchoolChanged,
                schools = schools
            )
        }
    }
}

@Composable
fun ExpandableSchoolDropdown(
    selectedSchool: String,
    expandedSchool: Boolean,
    onSchoolSelected: (String) -> Unit,
    onExpandedSchoolChanged: (Boolean) -> Unit,
    schools: List<String>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedSchool,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        IconButton(
            onClick = { onExpandedSchoolChanged(!expandedSchool) }
        ) {
            Icon(
                imageVector = if (expandedSchool) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(
            visible = expandedSchool,
            enter = expandVertically() + fadeIn(),
            exit = fadeIn() + expandVertically()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
            ) {
                schools.forEach { school ->
                    Text(
                        text = school,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clickable { onSchoolSelected(school) }
                            .background(
                                if (selectedSchool == school) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else Color.Transparent
                            )
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, alpha = 0.2f)
                }
            }
        }
    }
}

@Composable
fun OwnerModeEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = NeonRed,
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.no_registrations_found),
            color = NeonRed,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(R.string.no_registrations_subtext),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
fun OwnerModeRegistrationsList(
    registrations: List<AdminRegistration>,
    onDeleteRegistration: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(registrations) { registration ->
            OwnerModeRegistrationItem(
                registration = registration,
                onDeleteRegistration = onDeleteRegistration
            )
            Divider(color = MaterialTheme.colorScheme.outlineVariant, alpha = 0.2f)
        }
    }
}

@Composable
fun OwnerModeRegistrationItem(
    registration: AdminRegistration,
    onDeleteRegistration: (String) -> Unit
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
                .padding(12.dp)
        ) {
            Text(
                text = registration.fullName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${stringResource(R.string.school_label)} ${registration.schoolName}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = "${stringResource(R.string.roll_number_label)} ${registration.rollNumber}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = formatTimestamp(registration.timestamp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        IconButton(
            onClick = { onDeleteRegistration(registration.id) },
            color = MaterialTheme.colorScheme.error
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onError
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
    return sdf.format(date)
}