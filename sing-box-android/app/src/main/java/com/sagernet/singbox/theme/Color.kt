package com.sagernet.singbox.theme

import androidx.compose.ui.graphics.Color

/**
 * iOS System Colors - Exact matches for iOS design
 * Reference: https://developer.apple.com/design/human-interface-guidelines/color
 */
object IOSColors {
    // System Colors
    val Blue = Color(0xFF007AFF)
    val Green = Color(0xFF34C759)
    val Indigo = Color(0xFF5856D6)
    val Orange = Color(0xFFFF9500)
    val Pink = Color(0xFFFF2D55)
    val Purple = Color(0xFFAF52DE)
    val Red = Color(0xFFFF3B30)
    val Teal = Color(0xFF5AC8FA)
    val Yellow = Color(0xFFFFCC00)

    // Gray Colors
    val Gray = Color(0xFF8E8E93)
    val Gray2 = Color(0xFFAEAEB2)
    val Gray3 = Color(0xFFC7C7CC)
    val Gray4 = Color(0xFFD1D1D6)
    val Gray5 = Color(0xFFE5E5EA)
    val Gray6 = Color(0xFFF2F2F7)

    // Background Colors (Light)
    val Background = Color(0xFFF2F2F7) // systemGroupedBackground
    val SecondaryBackground = Color(0xFFFFFFFF)
    val TertiaryBackground = Color(0xFFF2F2F7)

    // Background Colors (Dark)
    val DarkBackground = Color(0xFF000000)
    val DarkSecondaryBackground = Color(0xFF1C1C1E)
    val DarkTertiaryBackground = Color(0xFF2C2C2E)

    // Surface Colors
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF2F2F7)
    val DarkSurface = Color(0xFF1C1C1E)
    val DarkSurfaceVariant = Color(0xFF2C2C2E)

    // Text Colors (Light)
    val TextPrimary = Color(0xFF000000)
    val TextSecondary = Color(0xFF3C3C43).copy(alpha = 0.6f)
    val TextTertiary = Color(0xFF3C3C43).copy(alpha = 0.3f)
    val TextQuaternary = Color(0xFF3C3C43).copy(alpha = 0.18f)

    // Text Colors (Dark)
    val DarkTextPrimary = Color(0xFFFFFFFF)
    val DarkTextSecondary = Color(0xFFEBEBF5).copy(alpha = 0.6f)
    val DarkTextTertiary = Color(0xFFEBEBF5).copy(alpha = 0.3f)
    val DarkTextQuaternary = Color(0xFFEBEBF5).copy(alpha = 0.18f)

    // Fill Colors (Light)
    val Fill = Color(0xFF3C3C43).copy(alpha = 0.2f)
    val Fill2 = Color(0xFF3C3C43).copy(alpha = 0.16f)
    val Fill3 = Color(0xFF3C3C43).copy(alpha = 0.12f)
    val Fill4 = Color(0xFF3C3C43).copy(alpha = 0.08f)

    // Fill Colors (Dark)
    val DarkFill = Color(0xFFEBEBF5).copy(alpha = 0.2f)
    val DarkFill2 = Color(0xFFEBEBF5).copy(alpha = 0.16f)
    val DarkFill3 = Color(0xFFEBEBF5).copy(alpha = 0.12f)
    val DarkFill4 = Color(0xFFEBEBF5).copy(alpha = 0.08f)

    // Separator Colors (Light)
    val Separator = Color(0xFF3C3C43).copy(alpha = 0.29f)
    val OpaqueSeparator = Color(0xFFC6C6C8)

    // Separator Colors (Dark)
    val DarkSeparator = Color(0xFF545458).copy(alpha = 0.65f)
    val DarkOpaqueSeparator = Color(0xFF38383A)

    // Tint Colors
    val TintColor = Blue
}

/**
 * Material 3 Color Scheme mapped to iOS colors
 */
object SingBoxColors {
    // Primary
    val Primary = IOSColors.Blue
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = IOSColors.Blue.copy(alpha = 0.12f)
    val OnPrimaryContainer = IOSColors.Blue

    // Secondary
    val Secondary = IOSColors.Indigo
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = IOSColors.Indigo.copy(alpha = 0.12f)
    val OnSecondaryContainer = IOSColors.Indigo

    // Tertiary
    val Tertiary = IOSColors.Orange
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = IOSColors.Orange.copy(alpha = 0.12f)
    val OnTertiaryContainer = IOSColors.Orange

    // Error
    val Error = IOSColors.Red
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = IOSColors.Red.copy(alpha = 0.12f)
    val OnErrorContainer = IOSColors.Red

    // Success
    val Success = IOSColors.Green
    val OnSuccess = Color(0xFFFFFFFF)
    val SuccessContainer = IOSColors.Green.copy(alpha = 0.12f)
    val OnSuccessContainer = IOSColors.Green

    // Warning
    val Warning = IOSColors.Orange
    val OnWarning = Color(0xFFFFFFFF)
    val WarningContainer = IOSColors.Orange.copy(alpha = 0.12f)
    val OnWarningContainer = IOSColors.Orange
}
