# Rube Aura Checker

An Android application built with Kotlin, Jetpack Compose, Material 3, and Supabase backend that simulates aura scanning with a unique owner mode feature.

## Features

### Core Features
- **First-launch Registration**: Collect user details (name, school, roll number) with validation
- **Aura Scanner**: Animated scanning interface with radar visualization
- **Aura Scoring**: Random scores 850-999 (ordinary) or 9,000,000 (owner mode)
- **Scan History**: Local history with filtering, sorting, and deletion
- **Settings**: Haptics, sound, animations, and accessibility preferences
- **Owner Mode**: Secret admin dashboard (PIN: 2012) with registration management

### Technical Architecture
- **Clean Architecture**: Presentation, Domain, Data layers
- **Dependency Injection**: Hilt for DI
- **State Management**: ViewModels with StateFlow
- **Navigation**: Navigation Compose with type-safe arguments
- **Local Storage**: DataStore for preferences and history
- **Backend**: Supabase integration via Retrofit (with test mode)
- **UI**: Jetpack Compose with Material 3, dark-only neon theme

### Security
- No embedded credentials - configuration via `gradle.properties`
- Admin operations require backend-authenticated tokens (not just client-side PIN)
- Test mode for development without real Supabase credentials

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/rubecoder/rubeaurachecker/
│   │   │   ├── core/                 # Core theme, navigation, UI components
│   │   │   ├── data/
│   │   │   │   ├── local/            # DataStore implementations
│   │   │   │   └── remote/           # Supabase API & repositories
│   │   │   ├── di/                   # Hilt modules
│   │   │   ├── domain/
│   │   │   │   ├── model/            # Domain models
│   │   │   │   └── repository/       # Repository interfaces
│   │   │   └── presentation/         # Compose screens & ViewModels
│   │   │       ├── registration/
│   │   │       ├── home/
│   │   │       ├── scanner/
│   │   │       ├── result/
│   │   │       ├── history/
│   │   │       ├── settings/
│   │   │       ├── owner/
│   │   │       └── ownermode/
│   │   └── res/
│   ├── test/                         # Unit tests
│   └── androidTest/                  # UI tests
├── build.gradle.kts
└── proguard-rules.pro
```

## Setup

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Configuration
1. Copy `gradle.properties.example` to `gradle.properties` (local only, never commit)
2. Add your Supabase credentials:
   ```properties
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_ANON_KEY=your-anon-key
   SUPABASE_TEST_MODE=false
   ```

### Build
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run lint
./gradlew lintDebug
```

## Owner Mode

Access the admin dashboard by:
1. Long-press the small red dot at the bottom of the home screen
2. Enter PIN: `2012`
3. View/search/filter/delete registrations

**Note**: The PIN is client-side only for demonstration. Real admin auth uses backend-validated tokens.

## Testing

### Unit Tests
- `RegistrationViewModelTest` - Registration validation and persistence
- `ScannerViewModelTest` - Score generation and scan flow
- `OwnerViewModelTest` - PIN authentication and throttling
- `OwnerModeViewModelTest` - Admin token handling
- `SettingsViewModelTest` - Preferences management

### UI Tests
- `HistoryScreenTest` - History list, empty state, navigation, deletion

## Supabase Schema

```sql
-- Registrations table
CREATE TABLE registrations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name TEXT NOT NULL,
    school_name TEXT NOT NULL,
    roll_number TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE registrations ENABLE ROW LEVEL SECURITY;

-- Policy for public inserts (anon key)
CREATE POLICY "Allow public inserts" ON registrations
    FOR INSERT WITH CHECK (true);

-- Admin functions (Edge Functions) for owner operations
-- authenticate-admin, get-all-registrations, delete-registration, etc.
```

## License

MIT License - See LICENSE file for details.

## Disclaimer

This app is for entertainment purposes only. Aura scores are randomly generated within a positive range.# Last updated: Mon, Oct 12, 2026 10:20:44 PM
