# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Development Tasks

### Building the Application
- Debug build: `./gradlew assembleDebug`
- Release build: `./gradlew assembleRelease`

### Running Tests
- Unit tests: `./gradlew test`
- UI tests (Android instrumented): `./gradlew connectedAndroidTest`
- Specific test class: `./gradlew test --tests "*OwnerModeViewModelTest*"`

### Code Quality
- Lint: `./gradlew lintDebug`
- Format Kotlin: `./gradlew ktlintFormat`

### Cleaning
- Clean build: `./gradlew clean`

## Code Architecture and Structure

### Overall Architecture
The application follows Clean Architecture principles with three main layers:
1. **Presentation Layer**: Jetpack Compose UI, ViewModels, navigation
2. **Domain Layer**: Use cases, repositories (interfaces), domain models
3. **Data Layer**: Implementations of repositories (local and remote), data sources

### Key Packages
- `com.rubecoder.rubeaurachecker.core`: Theme, navigation, shared UI components
- `com.rubecoder.rubeaurachecker.data`: 
  - `local`: DataStore implementations for preferences and history
  - `remote`: Supabase API interfaces and remote data sources
- `com.rubecoder.rubeaurachecker.di`: Hilt modules for dependency injection
- `com.rubecoder.rubeaurachecker.domain`:
  - `model`: Data classes representing business entities
  - `repository`: Interfaces defining data operations
- `com.rubecoder.rubeaurachecker.presentation`:
  - Feature-specific packages (registration, home, scanner, result, history, settings, owner, ownermode)
  - Each contains ViewModels and Compose screens

### State Management
- ViewModels use `StateFlow` and `MutableStateFlow` for UI state
- Hilt ViewModels for dependency injection
- SavedStateHandle used for handling navigation arguments (e.g., admin token)

### Navigation
- Single-activity architecture with Navigation Compose
- Type-safe arguments via navArgument (e.g., for owner mode token)
- Navigation graph defined in `NavHost.kt`

### Dependency Injection
- Hilt for DI across the application
- `@Singleton` scopes for repositories and data sources
- `@HiltViewModel` for ViewModel injection

### Local Persistence
- DataStore (Preferences) for:
  - Registration completion status and draft
  - User preferences (haptics, sound, animations)
  - Scan history (serialized as JSON strings in a string set)
- No Room database used; history stored as JSON in DataStore for simplicity

### Remote Data
- Supabase backend via Retrofit
- Test mode switches between local DataStore and remote Supabase
- Anonymous key for public operations (registrations)
- Backend-authenticated endpoints for admin operations (requires valid JWT)

### UI/UX
- Material 3 with custom dark-only neon theme (black background, NeonRed accents)
- Compose animations for scanner radar, pulse effects, and transitions
- Accessibility considerations: content descriptions, touch targets, screen reader friendly
- Responsive layouts using Modifier.weight(), fillMaxSize(), and padding

### Security Notes
- No embedded credentials: Supabase configuration via gradle.properties (not committed)
- Admin operations require backend-validated tokens (not just client-side PIN)
- Test mode allows development without real Supabase credentials
- PIN for owner mode is client-side only for demonstration; real auth uses backend

## Commonly Used Files
- `app/build.gradle.kts`: Dependencies, build configuration, Supabase config via buildConfigFields
- `app/src/main/java/com/rubecoder/rubeaurachecker/core/theme/RubeAuraTheme.kt`: Dark-only neon theme definition
- `app/src/main/java/com/rubecoder/rubeaurachecker/core/navigation/NavHost.kt`: Navigation graph with all destinations
- `app/src/main/java/com/rubecoder/rubeaurachecker/presentation/ownermode/OwnerModeScreen.kt`: Admin dashboard with registration management
- `app/src/main/java/com/rubecoder/rubeaurachecker/presentation/history/HistoryScreen.kt`: Scan history with filtering and sorting
- `app/src/main/java/com/rubecoder/rubeaurachecker/presentation/settings/SettingsScreen.kt`: User preferences and accessibility options

## Testing Strategy
- Unit tests for ViewModels in `src/test/java`
- UI tests for key screens in `src/androidTest/java`
- Mock repositories using Mockito for isolated ViewModel testing
- Tests cover validation, state transitions, and error handling

## Building for Release
1. Ensure `gradle.properties` contains real Supabase credentials (not the example)
2. Set `SUPABASE_TEST_MODE=false` in gradle.properties
3. Run `./gradlew assembleRelease`
4. The unsigned APK will be in `app/build/outputs/apk/release/`