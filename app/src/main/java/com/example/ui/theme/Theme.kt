package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- PRESETS OF PREMIUM CULINARY THEMES ---

// 1. SUNSET RICE / CƠM TẤM SUNSET (Amber orange, cozy, warm)
val SunsetLight = lightColorScheme(
    primary = Color(0xFFE65100),         // Rich dark orange
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCC80), // Soft warm cream orange
    onPrimaryContainer = Color(0xFF3E0A00),
    secondary = Color(0xFFF57C00),       // Warm amber
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3E0),
    onSecondaryContainer = Color(0xFF4E1D00),
    tertiary = Color(0xFF8D6E63),        // Toast clay
    onTertiary = Color.White,
    background = Color.White,      // Base white background
    onBackground = Color.Black,    // Base black text
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF7F1EB),   // Light warm gray
    onSurfaceVariant = Color(0xFF5C544F)
)

val SunsetDark = darkColorScheme(
    primary = Color(0xFFFFB74D),
    onPrimary = Color(0xFF5D1F00),
    primaryContainer = Color(0xFFE65100),
    onPrimaryContainer = Color(0xFFFFE0B2),
    secondary = Color(0xFFFFCC80),
    onSecondary = Color(0xFF4E1D00),
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF7F1EB),
    onSurfaceVariant = Color(0xFF5C544F)
)

// 2. ORGANIC BAMBOO / CƠM LAM TÂY BẮC (Zen green, clean, forest food)
val BambooLight = lightColorScheme(
    primary = Color(0xFF2E7D32),         // Forest Green 800
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA5D6A7), // Lily soft green
    onPrimaryContainer = Color(0xFF002204),
    secondary = Color(0xFF689F38),       // Bamboo light green
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2F0D9),
    onSecondaryContainer = Color(0xFF1B2E0B),
    tertiary = Color(0xFF795548),        // Wooden clay
    onTertiary = Color.White,
    background = Color.White,      // Base white background
    onBackground = Color.Black,    // Base black text
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFEAF0E9),
    onSurfaceVariant = Color(0xFF4C534B)
)

val BambooDark = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF0C2B0E),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFAED581),
    onSecondary = Color(0xFF21330E),
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFEAF0E9),
    onSurfaceVariant = Color(0xFF4C534B)
)

// 3. CLAYPOT HONEY / CƠM NIÊU ĐẤT (Terracotta reddish clay & gold saffron)
val ClaypotLight = lightColorScheme(
    primary = Color(0xFFBF360C),         // Terracotta Red 900
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCCBC),
    onPrimaryContainer = Color(0xFF3D0C00),
    secondary = Color(0xFFFF9800),       // Deep Honey Yellow
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFE082),
    onSecondaryContainer = Color(0xFF3D2000),
    tertiary = Color(0xFF795548),        // Earthy loam
    onTertiary = Color.White,
    background = Color.White,      // Base white background
    onBackground = Color.Black,    // Base black text
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFFCEAE5),
    onSurfaceVariant = Color(0xFF61514C)
)

val ClaypotDark = darkColorScheme(
    primary = Color(0xFFFF8A65),
    onPrimary = Color(0xFF3D0C00),
    primaryContainer = Color(0xFFBF360C),
    onPrimaryContainer = Color(0xFFFFCCBC),
    secondary = Color(0xFFFFD54F),
    onSecondary = Color(0xFF3D2000),
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFFCEAE5),
    onSurfaceVariant = Color(0xFF61514C)
)

// 4. MODERN HANOI NIGHTS / BẾP HIỆN ĐẠI (Tech-savvy purple-pink-indigo)
val ModernIndigoLight = lightColorScheme(
    primary = Color(0xFF1A237E),         // Deeper Rich Indigo
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2D6F3), // Soft light-indigo contrast
    onPrimaryContainer = Color(0xFF0D1442),
    secondary = Color(0xFFAD1457),       // Rich Pink-Rose
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF8BBD0),
    onSecondaryContainer = Color(0xFF4A0022),
    tertiary = Color(0xFF006064),        // Deep Teal
    onTertiary = Color.White,
    background = Color.White,      // Base white background
    onBackground = Color.Black,    // Base black text
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE3E5EB),
    onSurfaceVariant = Color(0xFF2B2E3E)
)

val ModernIndigoDark = darkColorScheme(
    primary = Color(0xFF7986CB),         // Solid medium-dark indigo
    onPrimary = Color(0xFF0A0E3C),
    primaryContainer = Color(0xFF1A237E), // Rich Deep Indigo
    onPrimaryContainer = Color(0xFFE8EAF6),
    secondary = Color(0xFFD81B60),       // Deep Rich Rose Pink
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE3E5EB),
    onSurfaceVariant = Color(0xFF2B2E3E)
)

// 5. PEARL LIGHT / CƠM NGON TRẮNG SÁNG (White app theme, clean partitions, crisp black icons)
val PearlLight = lightColorScheme(
    primary = Color(0xFF111111),         // Deep carbon black
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEBEBEB), // Sleek light gray partitions
    onPrimaryContainer = Color(0xFF111111),
    secondary = Color(0xFF333333),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5F5F5),
    onSecondaryContainer = Color(0xFF111111),
    tertiary = Color(0xFF666666),
    onTertiary = Color.White,
    background = Color.White,      // Base white background
    onBackground = Color.Black,    // Base black text
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF0F0F0),   // Demarcated lines and borders
    onSurfaceVariant = Color(0xFF333333)
)

val PearlDark = darkColorScheme(
    primary = Color(0xFFEEEEEE),
    onPrimary = Color(0xFF111111),
    primaryContainer = Color(0xFF333333),
    onPrimaryContainer = Color(0xFFEEEEEE),
    secondary = Color(0xFFDDDDDD),
    onSecondary = Color(0xFF111111),
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF333333)
)

// 6. FOODGO / ĐỎ CORAL QUYẾN RŨ (Perfect matching red-coral aesthetics in screenshot)
val FoodGoLight = lightColorScheme(
    primary = Color(0xFFEF3C46),         // Vibrant red-coral
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEBEC), // Soft pink-red highlight
    onPrimaryContainer = Color(0xFF800C12),
    secondary = Color(0xFFEF3C46),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9), // Light slate grey
    onSecondaryContainer = Color(0xFF1E293B),
    tertiary = Color(0xFF334155),
    onTertiary = Color.White,
    background = Color.White,      // Base white background
    onBackground = Color.Black,    // Base black text
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569)
)

val FoodGoDark = darkColorScheme(
    primary = Color(0xFFEF4D56),         // Slightly brighter for contrast on dark
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4E0E11),
    onPrimaryContainer = Color(0xFFFFE1E2),
    secondary = Color(0xFFEF4D56),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
    themeName: String = "foodgo",
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (themeName.lowercase()) {
        "foodgo" -> if (darkTheme) FoodGoDark else FoodGoLight
        "bamboo" -> if (darkTheme) BambooDark else BambooLight
        "claypot" -> if (darkTheme) ClaypotDark else ClaypotLight
        "indigo" -> if (darkTheme) ModernIndigoDark else ModernIndigoLight
        "pearl" -> if (darkTheme) PearlDark else PearlLight
        "sunset" -> if (darkTheme) SunsetDark else SunsetLight
        else -> if (darkTheme) FoodGoDark else FoodGoLight
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
