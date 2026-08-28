package br.com.hugolumazzini.havalapkstore.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta medida pixel a pixel da interface do Impulse, para o APK Box parecer
 * parte do mesmo conjunto quando os dois convivem na central.
 */
object Cores {
    /** Fundo do painel de conteúdo — o tom mais escuro da tela. */
    val Fundo = Color(0xFF0A0A0C)

    /** Barra lateral: um degrau acima do fundo, levemente azulada. */
    val Lateral = Color(0xFF0D0E12)

    /** Cartões e blocos de conteúdo. */
    val Superficie = Color(0xFF12141A)

    /** Item selecionado na lateral: azul bem rebaixado. */
    val SuperficieSelecionada = Color(0xFF152233)

    /** Campos de texto e áreas de entrada. */
    val Campo = Color(0xFF262A33)

    /** Azul de destaque: seleção, links e ação primária. */
    val Destaque = Color(0xFF4A9EFF)

    /** Verde de confirmação: ação secundária e estado instalado. */
    val Confirmacao = Color(0xFF34C759)

    /** Vermelho de erro, no mesmo registro do iOS/Impulse. */
    val Erro = Color(0xFFFF453A)

    /** Texto de maior peso: títulos e nomes de app. */
    val Texto = Color(0xFFF5F5F5)

    /** Texto corrido. */
    val TextoCorrido = Color(0xFFE4E4E5)

    /** Texto de apoio, rótulos de seção e descrições. */
    val TextoApoio = Color(0xFF8A93A3)

    /** Divisores e contornos discretos. */
    val Contorno = Color(0xFF262A33)
}
