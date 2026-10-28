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

/**
 * Ícone de histórico/versões — um relógio com seta circular.
 * Usado na aba de versões do Haval Trip.
 */
val IconeHistorico: ImageVector by lazy {
    ImageVector.Builder(
        name = "History",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(fill = SolidColor(Color.White)) {
            // Círculo do relógio
            moveTo(13f, 3f)
            arcTo(9f, 9f, 0f, false, false, 13f, 21f)
            arcTo(9f, 9f, 0f, false, false, 13f, 3f)
            close()
            moveTo(13f, 5f)
            arcTo(7f, 7f, 0f, true, true, 6f, 12f)
            arcTo(7f, 7f, 0f, false, true, 13f, 5f)
            close()
            // Ponteiros
            moveTo(12.5f, 8f)
            verticalLineTo(13f)
            lineTo(16.2f, 15.2f)
            lineTo(15.2f, 16.7f)
            lineTo(11f, 14f)
            verticalLineTo(8f)
            close()
        }
    }.build()
}
