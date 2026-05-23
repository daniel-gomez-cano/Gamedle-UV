package com.gamedleuv.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gamedleuv.R
import com.gamedleuv.domain.model.User
import com.gamedleuv.ui.components.AppButton
import com.gamedleuv.ui.components.BlurredImage
import com.gamedleuv.ui.components.DropdownField
import com.gamedleuv.ui.components.HeartsRow
import com.gamedleuv.ui.components.ProfileButton
import com.gamedleuv.ui.theme.GamedleUVTheme
import com.gamedleuv.ui.viewmodel.AuthViewModel
import com.gamedleuv.ui.viewmodel.GameHint
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
        streak = gameState.streak,
        hintUnlocked = gameState.hintUnlocked,
        hintUsed = gameState.hintUsed,
        currentHint = gameState.currentHint,
        isGameOver = gameState.isGameOver,
        onGameSelected = gameViewModel::onGameSelected,
        onSkip = gameViewModel::onSkip,
        onSearchQueryChange = gameViewModel::searchGames,
        onGuess = gameViewModel::onGuess,
        onUseHint = gameViewModel::onUseHint,
        onDismissGameOver = gameViewModel::onDismissGameOver,
        onDismissHint = gameViewModel::onDismissHint,       // ← nuevo
        onReopenHint = gameViewModel::onReopenHint,
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
    revealedSectors: List<Int>,
    streak: Int,
    hintUnlocked: Boolean,
    hintUsed: Boolean,
    currentHint: GameHint?,
    isGameOver: Boolean,
    onGameSelected: (String) -> Unit,
    onSkip: () -> Unit,
    onGuess: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onUseHint: () -> Unit,
    onDismissHint: () -> Unit,
    onReopenHint: () -> Unit,
    onDismissGameOver: () -> Unit,
    gameImageUrl: String?
) {

    if (isGameOver) {
        AlertDialog(
            onDismissRequest = onDismissGameOver,
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "¡Perdiste!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Se acabaron tus corazones.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Racha final: $streak",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onDismissGameOver) {
                    Text(
                        text = "Volver a jugar",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }


    if (currentHint != null) {
        AlertDialog(
            onDismissRequest = onDismissHint ,
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Pista",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HintRow(label = "Año",         value = currentHint.releaseYear)
                    HintRow(label = "Distribuidor", value = currentHint.publisher)
                    HintRow(label = "Género",       value = currentHint.genre)
                }
            },
            confirmButton = {
                TextButton(onClick = onDismissHint) {
                    Text("Entendido", color = MaterialTheme.colorScheme.tertiary)
                }
            }
        )
    }

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
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp)) // Espacio para bajar el header

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

                Spacer(modifier = Modifier.height(24.dp))

                // USUARIO + VIDAS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val imageData = remember(user?.profilePictureUrl) {
                            user?.profilePictureUrl?.takeIf { it.isNotEmpty() }?.let { url ->
                                val base64 = url.substringAfter("base64,")
                                android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                            }
                        }
                        if (imageData != null) {
                            AsyncImage(
                                model = imageData,
                                contentDescription = "avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "avatar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                        }
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


                BlurredImage(
                    imageUrl = gameImageUrl,
                    revealedSectors = revealedSectors,
                    modifier = Modifier
                        .fillMaxHeight(0.65f) // Un poquito más grande el rectángulo
                        .aspectRatio(2f / 3f) // Relación de aspecto correcto para las de portadas
                        .align(Alignment.CenterHorizontally)
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
                        text = when {
                            hintUnlocked && !hintUsed -> "Pista"   // disponible para usar
                            hintUsed                  -> "Ver pista"  // ya usada, se puede revisar
                            else                      -> "Pista"      // bloqueada
                        },
                        onClick = when {
                            hintUnlocked && !hintUsed -> onUseHint    // primera vez: consume y muestra
                            hintUsed                  -> onReopenHint // ya usada: solo reabre el dialog
                            else                      -> ({ })        // bloqueada: no hace nada
                        },
                        modifier = Modifier
                            .border(
                                width = 4.dp,
                                color = when {
                                    hintUnlocked && !hintUsed -> MaterialTheme.colorScheme.tertiary
                                    hintUsed                  -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                                    else                      -> Color.Red
                                },
                                shape = RoundedCornerShape(50.dp)
                            )
                            .weight(1f)
                            .height(50.dp)
                    )

                    Text(
                        text = "Racha:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    // RF-10: racha desde el ViewModel, no desde User
                    Text(
                        text = "$streak",
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

// Fila auxiliar para el dialog de pista
@Composable
private fun HintRow(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SoloGameScreenPreview() {
    GamedleUVTheme {
        SoloGameContent(
            user = User(id = "1", email = "test@test.com", username = "Player One"),
            lives = 3,
            maxLives = 5,
            gameList = listOf("The Legend of Zelda", "Super Mario Bros", "Metroid"),
            selectedGame = "",
            searchQuery = "",
            isLoading = false,
            revealedSectors = listOf(1, 4, 7),
            streak = 5,
            hintUnlocked = true,
            hintUsed = false,
            currentHint = null,
            isGameOver = false,
            onGameSelected = {},
            onSkip = {},
            onGuess = {},
            onSearchQueryChange = {},
            onUseHint = {},
            onDismissHint = {},
            onReopenHint = {},
            onDismissGameOver = {},
            gameImageUrl = null
        )
    }
}

