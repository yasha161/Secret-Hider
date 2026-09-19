package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeStyle(val displayName: String, val dotColor: Color) {
  OCEAN_BLUE("Ocean Royal (Default)", BluePrimary),
  PINK_PEACH("Pink Peach Blossom 🌸🍑", PeachPrimary),
  MIDNIGHT_BLACK("Midnight Black 🖤", MidnightPrimary),
  ACID_CYBER("Acid Cyber Gen-Z ⚡", AcidLimePrimary),
  CYBER_VIOLET("Cyber Violet 🔮", VioletPrimary)
}

val OceanBlueColorScheme: ColorScheme = lightColorScheme(
  primary = BluePrimary,
  onPrimary = Color.White,
  primaryContainer = BlueSurfaceVariant,
  onPrimaryContainer = BluePrimaryVariant,
  secondary = BlueSecondary,
  onSecondary = Color.White,
  tertiary = BlueTertiary,
  background = BlueBackground,
  onBackground = BlueOnBackground,
  surface = BlueSurface,
  onSurface = BlueOnSurface,
  surfaceVariant = BlueSurfaceVariant,
  onSurfaceVariant = Color(0xFF475569),
  outline = BlueOutline
)

val PinkPeachColorScheme: ColorScheme = lightColorScheme(
  primary = PeachPrimary,
  onPrimary = Color.White,
  primaryContainer = PeachSurfaceVariant,
  onPrimaryContainer = PeachOnSurface,
  secondary = PeachSecondary,
  onSecondary = Color.White,
  tertiary = PeachTertiary,
  background = PeachBackground,
  onBackground = PeachOnBackground,
  surface = PeachSurface,
  onSurface = PeachOnSurface,
  surfaceVariant = PeachSurfaceVariant,
  onSurfaceVariant = Color(0xFF9F1239),
  outline = PeachOutline
)

val MidnightBlackColorScheme: ColorScheme = darkColorScheme(
  primary = MidnightPrimary,
  onPrimary = Color.Black,
  primaryContainer = MidnightSurfaceVariant,
  onPrimaryContainer = MidnightPrimary,
  secondary = MidnightSecondary,
  onSecondary = Color.Black,
  tertiary = MidnightTertiary,
  background = MidnightBackground,
  onBackground = MidnightOnBackground,
  surface = MidnightSurface,
  onSurface = MidnightOnSurface,
  surfaceVariant = MidnightSurfaceVariant,
  onSurfaceVariant = Color(0xFF9CA3AF),
  outline = MidnightOutline
)

val CyberVioletColorScheme: ColorScheme = lightColorScheme(
  primary = VioletPrimary,
  onPrimary = Color.White,
  primaryContainer = VioletSurfaceVariant,
  onPrimaryContainer = VioletPrimary,
  secondary = VioletSecondary,
  onSecondary = Color.White,
  tertiary = VioletTertiary,
  background = VioletBackground,
  onBackground = VioletOnBackground,
  surface = VioletSurface,
  onSurface = VioletOnSurface,
  surfaceVariant = VioletSurfaceVariant,
  onSurfaceVariant = VioletOnBackground,
  outline = VioletOutline
)

val AcidCyberColorScheme: ColorScheme = darkColorScheme(
  primary = AcidLimePrimary,
  onPrimary = Color(0xFF090D14),
  primaryContainer = Color(0xFF1C2D16),
  onPrimaryContainer = AcidLimePrimary,
  secondary = AcidLimeSecondary,
  onSecondary = Color(0xFF090D14),
  tertiary = AcidLimeTertiary,
  background = AcidLimeBackground,
  onBackground = AcidLimeOnBackground,
  surface = AcidLimeSurface,
  onSurface = AcidLimeOnSurface,
  surfaceVariant = AcidLimeSurfaceVariant,
  onSurfaceVariant = Color(0xFF94A3B8),
  outline = Color(0xFF334155)
)

@Composable
fun SecretHiderTheme(
  themeStyle: AppThemeStyle = AppThemeStyle.OCEAN_BLUE,
  content: @Composable () -> Unit
) {
  val colorScheme = when (themeStyle) {
    AppThemeStyle.OCEAN_BLUE -> OceanBlueColorScheme
    AppThemeStyle.PINK_PEACH -> PinkPeachColorScheme
    AppThemeStyle.MIDNIGHT_BLACK -> MidnightBlackColorScheme
    AppThemeStyle.ACID_CYBER -> AcidCyberColorScheme
    AppThemeStyle.CYBER_VIOLET -> CyberVioletColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}


