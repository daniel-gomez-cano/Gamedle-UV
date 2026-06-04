package com.gamedleuv.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gamedleuv.R
import com.gamedleuv.ui.components.CountdownTimer
import com.gamedleuv.ui.components.DropdownField
import com.gamedleuv.ui.components.HeartsRow
import com.gamedleuv.ui.components.ProfileButton
import com.gamedleuv.ui.viewmodel.AuthViewModel
import com.gamedleuv.ui.viewmodel.enums.PvpGameResult
import com.gamedleuv.ui.viewmodel.RoomViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.gamedleuv.ui.components.BlurredImage

@Composable
fun PvpGameScreen(
    authViewModel: AuthViewModel,
    roomViewModel: RoomViewModel,
    onGameOver: () -> Unit
) {
    val user by authViewModel.currentUser.collectAsState()
    val state by roomViewModel.uiState.collectAsState()
    val room = state.room

    // Navega a game over cuando el dialog se cierra después de mostrar el resultado
    LaunchedEffect(state.showResultDialog) {
        if (state.gameResult != null && !state.showResultDialog) {
            onGameOver()
        }
    }
    val myUid = state.myUid
    val players = room?.players ?: emptyMap()
    val me = players.values.firstOrNull { it.uid == myUid }
    val rival = players.values.firstOrNull { it.uid != myUid }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (room == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {

            // Determinar si se muestra el dialog de resultado
            val gameResult = state.gameResult
            if (state.showResultDialog && gameResult != null) {
                PvpResultDialog(
                    result = gameResult,
                    onDismiss = { roomViewModel.onDismissResult() }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)

            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())  // ← va aquí dentro
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // HEADER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.virus),
                            contentDescription = "logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GAMEDLE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                shadow = Shadow(
                                    color = Color(0xFFB298DC),
                                    offset = Offset(4f, 4f),
                                    blurRadius = 4f
                                )
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // CONTADOR
                    // El valor viene del ViewModel; la UI solo lo renderiza
                    CountdownTimer(
                        seconds = state.remainingSeconds,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // JUGADORES Y VIDAS
                    // JUGADORES Y VIDAS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Jugador (izquierda)
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val myImageData = remember(me?.profilePictureUrl) {
                                    me?.profilePictureUrl?.takeIf { it.isNotEmpty() }?.let { url ->
                                        val base64 = url.substringAfter("base64,")
                                        android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                                    }
                                }
                                if (myImageData != null) {
                                    AsyncImage(
                                        model = myImageData,
                                        contentDescription = "avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.profile),
                                        contentDescription = "avatar",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = me?.username ?: user?.username ?: "Tú",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            HeartsRow(total = 5, filled = me?.lives ?: 5, size = 28)
                        }

                        // VS (Centro)
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        // Rival (derecha)
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                val rivalImageData = remember(rival?.profilePictureUrl) {
                                    rival?.profilePictureUrl?.takeIf { it.isNotEmpty() }?.let { url ->
                                        val base64 = url.substringAfter("base64,")
                                        android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                                    }
                                }
                                if (rivalImageData != null) {
                                    AsyncImage(
                                        model = rivalImageData,
                                        contentDescription = "avatar rival",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.profile),
                                        contentDescription = "avatar rival",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = rival?.username ?: "Rival",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            HeartsRow(total = 5, filled = rival?.lives ?: 5, size = 28)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // IMAGEN DEL JUEGO con blur sincronizado
                    BlurredImage(
                        imageUrl = room.gameImageUrl,
                        revealedSectors = room.revealedSectors, // ← viene de Firebase
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.45f)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // RONDA
                    Text(
                        text = "Ronda ${room.currentRound}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    
                    Spacer(modifier = Modifier.height(30.dp))

                    // DROPDOWN + ENVIAR
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DropdownField(
                            options = state.gameList,
                            selected = state.searchQuery,
                            query = state.searchQuery,
                            onSelectedChange = { roomViewModel.onGameSelected(it) },
                            onValueChange = { roomViewModel.searchGames(it) },
                            modifier = Modifier.weight(1f)
                        )
                        ProfileButton(
                            img = R.drawable.arrow,
                            transparent = true,
                            iconSize = 24.dp,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { roomViewModel.onGuess() },
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(200.dp))
                }
            }
        }
    }
}

// Dialogo de resultado
@Composable
private fun PvpResultDialog(
    result: PvpGameResult,
    onDismiss: () -> Unit
) {
    val (title, message, titleColor) = when (result) {
        PvpGameResult.WIN  -> Triple(
            "¡Ganaste!",
            "Derrotaste a tu rival. ¡Bien jugado!",
            MaterialTheme.colorScheme.primary
        )
        PvpGameResult.LOSE -> Triple(
            "¡Perdiste!",
            "Tu rival te ganó esta vez. ¡Vuelve a intentarlo!",
            MaterialTheme.colorScheme.error
        )
        PvpGameResult.DRAW -> Triple(
            "¡Empate!",
            "Ambos llegaron al límite al mismo tiempo.",
            MaterialTheme.colorScheme.tertiary
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = titleColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Volver al inicio",
                    color = titleColor
                )
            }
        }
    )
}
