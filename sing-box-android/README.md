# sing-box for Android

Experimental Android client for sing-box, the universal proxy platform.

This is a port of the iOS/macOS version (sing-box-for-apple) to Android, maintaining identical UI and functionality.

## Features

- **Dashboard**: Real-time monitoring of VPN status, traffic, and connections
- **Logs**: Real-time log viewing with search and filtering
- **Tools**: Network diagnostics (STUN test, network quality)
- **Settings**: Comprehensive configuration options
- **VPN Service**: Background VPN service with notification
- **Quick Settings Tile**: Toggle VPN from notification shade
- **Boot Auto-Start**: Optional auto-start on device boot

## Architecture

The app follows the MVVM architecture with Clean Architecture principles:

```
app/
├── ui/           # UI layer (Compose)
│   ├── dashboard/
│   ├── logs/
│   ├── tools/
│   └── settings/
├── viewmodel/    # ViewModels
└── theme/        # Theme and styling

core/
├── database/     # Room database
└── shared/       # Shared utilities

service/
└── service/      # VPN and background services
```

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Database**: Room
- **Asynchronous**: Kotlin Coroutines + Flow
- **VPN**: Android VpnService

## Building

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Clean build
./gradlew clean
```

## Project Structure

```
sing-box-android/
├── app/                    # Main application module
│   ├── src/main/
│   │   ├── java/          # Kotlin source code
│   │   └── res/           # Resources
│   └── build.gradle.kts
├── core/                   # Core library module
│   ├── src/main/
│   │   └── java/          # Core business logic
│   └── build.gradle.kts
├── service/                # Service module
│   ├── src/main/
│   │   └── java/          # VPN and background services
│   └── build.gradle.kts
├── gradle/                 # Gradle wrapper
├── build.gradle.kts        # Root build file
├── settings.gradle.kts     # Project settings
└── README.md               # This file
```

## UI Design

The UI is designed to match the iOS/macOS version exactly:

- **Colors**: iOS system colors (Blue, Green, Orange, Red, etc.)
- **Typography**: iOS text styles (Large Title, Title 1-3, Body, etc.)
- **Layout**: Same spacing, padding, and corner radius values
- **Animations**: Matching spring and tween animations
- **Components**: Same card designs, navigation patterns, and interactions

## Key Components

### Dashboard
- Status Card (VPN state, uptime)
- Profile Card (current profile selector)
- Connections Card (active connections count)
- Traffic Cards (upload/download speed and history)
- Clash Mode Card (Rule/Global/Direct)
- HTTP Proxy Card

### Logs
- Real-time log streaming
- Log level filtering (TRACE, DEBUG, INFO, WARN, ERROR, FATAL)
- Search functionality
- Export (clipboard, file, share)
- ANSI color support

### Tools
- Tailscale endpoints
- USB/IP services
- Network quality test
- STUN test
- Crash reports
- OOM reports

### Settings
- App settings (dark mode, notifications)
- Core settings (log level, memory limit)
- Packet Tunnel settings
- On Demand Rules
- Profile Override
- Remote Control
- Sponsors

## Integration with Libbox

To integrate the sing-box core library (Libbox):

1. Build Libbox for Android using gomobile:
   ```bash
   gomobile bind -target=android -o libbox.aar \
     -javapkg=io.nekohasekai.sagernet \
     github.com/SagerNet/sing-box/experimental/libbox
   ```

2. Add the AAR file to `app/libs/`

3. Update `app/build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(files("libs/libbox.aar"))
   }
   ```

4. Use the Libbox API in the VPN service

## Permissions

The app requires the following permissions:

- `INTERNET`: Network access
- `ACCESS_NETWORK_STATE`: Network state monitoring
- `FOREGROUND_SERVICE`: Background VPN service
- `RECEIVE_BOOT_COMPLETED`: Auto-start on boot
- `POST_NOTIFICATIONS`: Service notifications
- `CAMERA`: QR code scanning
- `QUERY_ALL_PACKAGES`: Per-app proxy support

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the GNU General Public License v3.0 - see the LICENSE file for details.

## Acknowledgments

- [sing-box](https://github.com/SagerNet/sing-box) - The universal proxy platform
- [sing-box-for-apple](https://github.com/nekohasekai/sing-box-for-apple) - iOS/macOS client
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Material Design 3](https://m3.material.io/) - Design system

## Support

For issues and questions:
- GitHub Issues: [Create an issue](https://github.com/your-repo/sing-box-android/issues)
- Documentation: [sing-box docs](https://sing-box.sagernet.org/)

---

**Note**: This is an experimental client. Use at your own risk.
