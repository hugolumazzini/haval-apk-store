package br.com.apkbox.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import br.com.apkbox.R

/**
 * Michroma para a marca e os rótulos de seção — a fonte larga e maquinal que o
 * Impulse usa nos títulos. Só existe em um peso, então nunca é negritada.
 */
val Michroma = FontFamily(Font(R.font.michroma_regular, FontWeight.Normal))

/** IBM Plex Sans para todo o resto: excelente legibilidade em tela pequena. */
val PlexSans = FontFamily(
    Font(R.font.ibm_plex_sans_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_sans_medium, FontWeight.Medium),
    Font(R.font.ibm_plex_sans_semibold, FontWeight.SemiBold),
    Font(R.font.ibm_plex_sans_bold, FontWeight.Bold),
)

/** A marca "APK BOX" no alto da barra lateral. */
val EstiloMarca = TextStyle(
    fontFamily = Michroma,
    fontSize = 21.sp,
    letterSpacing = 0.02.em,
    color = Cores.Texto,
)

/** Rótulo de seção: maiúsculas espaçadas, em cinza de apoio. */
val EstiloSecao = TextStyle(
    fontFamily = Michroma,
    fontSize = 15.sp,
    letterSpacing = 0.18.em,
    color = Cores.TextoApoio,
)

/**
 * Tela de carro, tocada em movimento e muitas vezes de relance: tudo um degrau
 * maior que o padrão do Material.
 */
val TipografiaCarro = Typography(
    headlineSmall = TextStyle(fontFamily = PlexSans, fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontFamily = PlexSans, fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontFamily = PlexSans, fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontFamily = PlexSans, fontSize = 15.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontFamily = PlexSans, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = PlexSans, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = PlexSans, fontSize = 13.sp),
    labelLarge = TextStyle(fontFamily = PlexSans, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontFamily = PlexSans, fontSize = 13.sp, fontWeight = FontWeight.Medium),
)
