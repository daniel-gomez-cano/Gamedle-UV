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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gamedleuv.R
import com.gamedleuv.domain.model.User
import com.gamedleuv.ui.components.AppButton
import com.gamedleuv.ui.components.BlurredImage
import com.gamedleuv.ui.components.DropdownField
import com.gamedleuv.ui.components.HeartsRow
import com.gamedleuv.ui.components.ProfileButton
import com.gamedleuv.ui.theme.GamedleUVTheme
import com.gamedleuv.ui.viewmodel.AuthViewModel
import com.gamedleuv.ui.viewmodel.GameViewModel

@Composable
fun SoloGameScreen(
    authViewModel: AuthViewModel,
    gameViewModel: GameViewModel
) {
    val user by authViewModel.currentUser.collectAsState()
    val gameState by gameViewModel.uiState.collectAsState()

    SoloGameContent(
        user = user,
        gameImageUrl = gameState.gameImageUrl,
        lives = gameState.lives,
        maxLives = gameState.maxLives,
        gameList = gameState.gameList,
        selectedGame = gameState.selectedGame,
        searchQuery = gameState.searchQuery,
        isLoading = gameState.isLoading,
        revealedSectors = gameState.revealedSectors,
        onGameSelected = gameViewModel::onGameSelected,
        onSkip = gameViewModel::onSkip,
        onSearchQueryChange = gameViewModel::searchGames,
        onGuess = gameViewModel::onGuess
    )
}

@Composable
private fun SoloGameContent(
    user: User?,
    lives: Int,
    maxLives: Int,
    gameList: List<String>,
    selectedGame: String,
    searchQuery: String,
    isLoading: Boolean,
    revealedSectors: List<Int>, // ← aquí sí va
    onGameSelected: (String) -> Unit,
    onSkip: () -> Unit,
    onGuess: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    gameImageUrl: String?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        if (isLoading) {
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
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GAMEDLE",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            shadow = Shadow(
                                color = Color(0xFFB298DC),
                                offset = Offset(4f, 4f),
                                blurRadius = 4f
                            )
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // USUARIO + VIDAS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "avatar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = user?.username ?: "Cargando...",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    HeartsRow(
                        total = maxLives,
                        filled = lives,
                        size = 30
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // IMAGEN DEL JUEGO con blur progresivo
                BlurredImage(
                    imageUrl = gameImageUrl,
                    revealedSectors = revealedSectors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // BOTONES + RACHA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppButton(
                        style = MaterialTheme.typography.labelMedium,
                        text = "Saltar",
                        onClick = onSkip,
                        modifier = Modifier
                            .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50.dp))
                            .weight(1f)
                            .height(50.dp)
                    )
                    AppButton(
                        style = MaterialTheme.typography.labelMedium,
                        text = "Pista",
                        onClick = {}, // TODO: Implementar lógica de pista
                        modifier = Modifier
                            .border(4.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(50.dp))
                            .weight(1f)
                            .height(50.dp)
                    )
                    Text(
                        text = "Racha:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${user?.currentStreak ?: 0}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // DROPDOWN + BOTÓN CONFIRMAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DropdownField(
                        options = gameList,
                        selected = selectedGame,
                        query = searchQuery,
                        onSelectedChange = { onGameSelected(it) },
                        onValueChange = { query -> onSearchQueryChange(query) },
                        modifier = Modifier.weight(1f)
                    )
                    ProfileButton(
                        img = R.drawable.arrow,
                        transparent = true,
                        iconSize = 24.dp,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = onGuess,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "Solo Game - Dark Mode", showSystemUi = true)
@Composable
fun PreviewSoloGameScreen() {
    GamedleUVTheme(darkTheme = true) {
        SoloGameContent(
            user = User(
                id = "preview",
                email = "test@test.com",
                username = "Kano065",
                currentStreak = 96
            ),
            gameImageUrl = "https://images.igdb.com/igdb/image/upload/t_cover_big/co1xyz.jpg",
            lives = 2,
            maxLives = 5,
            gameList = listOf("Elden Ring", "Hades", "Celeste"),
            selectedGame = "",
            isLoading = false,
            revealedSectors = listOf(0, 1, 2), // ← preview con fila superior revelada
            onGameSelected = {},
            onSkip = {},
            onGuess = {},
            onSearchQueryChange = {},
            searchQuery = ""
        )
    }
}