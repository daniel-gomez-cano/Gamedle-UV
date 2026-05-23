package com.gamedleuv.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Componente reutilizable de cuenta regresiva.
 *
 * Este componente es puramente de presentación: recibe [seconds] y lo muestra.
 * Toda la lógica del temporizador (coroutine, delay, decremento) vive en el
 * ViewModel correspondiente (p. ej. RoomViewModel.startCountdown).
 *
 * @param seconds   Segundos restantes a mostrar. Viene del UiState del ViewModel.
 * @param modifier  Modificador opcional para el Row contenedor.
 * @param color     Color del texto e ícono. Por defecto usa onBackground del tema.
 *
 * Uso:
 *   CountdownTimer(seconds = state.remainingSeconds)
 */
@Composable
fun CountdownTimer(
    seconds: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val displayColor = if (color == Color.Unspecified)
        MaterialTheme.colorScheme.onBackground
    else
        color

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$seconds s",
            style = MaterialTheme.typography.bodyMedium,
            color = displayColor
        )
    }
}