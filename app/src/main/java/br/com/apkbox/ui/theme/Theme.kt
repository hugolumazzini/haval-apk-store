package br.com.apkbox.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Azul = Color(0xFF4F8EF7)
private val AzulClaro = Color(0xFF86B6EF)

private val Escuro = darkColorScheme(
    primary = Azul,
    onPrimary = Color(0xFF07131F),
    primaryContainer = Color(0xFF15304F),
    onPrimaryContainer = AzulClaro,
    secondary = Color(0xFF9EB4CC),
    background = Color(0xFF101314),
    onBackground = Color(0xFFE8EAED),
    surface = Color(0xFF181C1E),
    onSurface = Color(0xFFE8EAED),
    surfaceVariant = Color(0xFF23282B),
    onSurfaceVariant = Color(0xFFB6BEC4),
    outline = Color(0xFF3A4145),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF3B0A06),
)

private val Claro = lightColorScheme(
    primary = Color(0xFF1A5FBF),
    onPrimary = Color.White,
    background = Color(0xFFF6F7F9),
    surface = Color.White,
    onSurface = Color(0xFF14181B),
    surfaceVariant = Color(0xFFE8ECF0),
    onSurfaceVariant = Color(0xFF444C52),
    outline = Color(0xFFC4CBD1),
)

// Tela de carro, tocada em movimento: tudo um degrau maior que o padrão.
private val TipografiaCarro = Typography(
    headlineSmall = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 17.sp),
    bodyMedium = TextStyle(fontSize = 15.sp),
    labelLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
)

@Composable
fun ApkBoxTheme(
    escuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (escuro) Escuro else Claro,
        typography = TipografiaCarro,
        content = content,
    )
}
