package br.com.hugolumazzini.havalapkstore.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Seta descendo para uma bandeja — o "baixar da internet" da aba Por URL.
 * Desenhado à mão para não arrastar a biblioteca inteira de ícones estendidos
 * por causa de um único glifo.
 */
val IconeDownload: ImageVector by lazy {
    ImageVector.Builder(
        name = "Download",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.White)) {
            // Haste e ponta da seta.
            moveTo(11f, 3f)
            horizontalLineTo(13f)
            verticalLineTo(12.17f)
            lineTo(16.59f, 8.59f)
            lineTo(18f, 10f)
            lineTo(12f, 16f)
            lineTo(6f, 10f)
            lineTo(7.41f, 8.59f)
            lineTo(11f, 12.17f)
            close()
        }
        path(fill = SolidColor(Color.White)) {
            // Bandeja.
            moveTo(5f, 18f)
            horizontalLineTo(19f)
            verticalLineTo(20f)
            horizontalLineTo(5f)
            close()
        }
    }.build()
}
