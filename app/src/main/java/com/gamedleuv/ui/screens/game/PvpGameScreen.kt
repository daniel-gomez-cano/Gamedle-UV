package com.gamedleuv.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gamedleuv.ui.components.AppButton
import com.gamedleuv.ui.components.DropdownField
import com.gamedleuv.ui.components.HeartsRow
import com.gamedleuv.ui.viewmodel.AuthViewModel
import com.gamedleuv.ui.viewmodel.RoomViewModel

@Composable
fun PvpGameScreen(
    authViewModel: AuthViewModel,
    roomViewModel: RoomViewModel,
    onGameOver: () -> Unit
) {
    val user by authViewModel.currentUser.collectAsState()
    val state by roomViewModel.uiState.collectAsState()
    val room = state.room

    // Cuando el status sea finished, navega a game over
    LaunchedEffect(room?.status) {
        if (room?.status == "finished") onGameOver()
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // VIDAS: yo vs rival
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = me?.username ?: user?.username ?: "Tú",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        HeartsRow(total = 5, filled = me?.lives ?: 5, size = 24)
                    }
                    Text(
                        text = "VS",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = rival?.username ?: "Rival",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        HeartsRow(total = 5, filled = rival?.lives ?: 5, size = 24)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // IMAGEN DEL JUEGO
                AsyncImage(
                    model = room.gameImageUrl,
                    contentDescription = "portada del juego",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.55f)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // RONDA
                Text(
                    text = "Ronda ${room.currentRound}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // BOTÓN SALTAR
                AppButton(
                    style = MaterialTheme.typography.labelMedium,
                    text = "Saltar",
                    onClick = { roomViewModel.onSkip() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // DROPDOWN + ENVIAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                    Button(
                        onClick = { roomViewModel.onGuess() },
                        modifier = Modifier.size(56.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text("✓", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}