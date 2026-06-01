package com.gamedleuv.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gamedleuv.R
import com.gamedleuv.ui.components.VideoBg
import com.gamedleuv.ui.theme.White
import com.gamedleuv.ui.viewmodel.AuthViewModel
import com.gamedleuv.ui.viewmodel.RoomViewModel

@Composable
fun LobbyScreen(
    authViewModel: AuthViewModel,
    roomViewModel: RoomViewModel,
    onGameReady: () -> Unit
) {
    val user by authViewModel.currentUser.collectAsState()
    val state by roomViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        roomViewModel.resetRoom()
    }

    // Cuando la sala tiene 2 jugadores, navega al juego
    LaunchedEffect(state.room?.status) {
        if (state.room?.status == "playing") onGameReady()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        VideoBg(videoResId = R.raw.fondo_auth, modifier = Modifier.fillMaxSize())

        if (state.isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else if (state.roomCode.isEmpty()) {
            // Pantalla inicial: crear o unirse
            LobbyInitialContent(
                onCreateRoom = {
                    val uid = user?.id ?: return@LobbyInitialContent
                    val username = user?.username ?: return@LobbyInitialContent
                    val profilePictureUrl = user?.profilePictureUrl ?: ""
                    roomViewModel.createRoom(uid, username, profilePictureUrl)
                },
                onJoinRoom = { code ->
                    val uid = user?.id ?: return@LobbyInitialContent
                    val username = user?.username ?: return@LobbyInitialContent
                    val profilePictureUrl = user?.profilePictureUrl ?: ""
                    roomViewModel.joinRoom(code, uid, username, profilePictureUrl)
                },
                error = state.error
            )
        } else {
            // Sala creada, esperando rival
            LobbyWaitingContent(code = state.roomCode)
        }
    }
}

@Composable
private fun LobbyInitialContent(
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit,
    error: String?
) {
    var joinCode by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "Modo PvP",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onCreateRoom,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = Color.White),
            border = BorderStroke(width = 4.dp, color = MaterialTheme.colorScheme.primary)
        ) {
            Text("Crear sala")
        }

        Text(
            text = "— o —",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = joinCode,
            onValueChange = { joinCode = it.uppercase() },
            label = { Text("Código de sala") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = Color.LightGray,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Button(
            onClick = { if (joinCode.length == 4) onJoinRoom(joinCode) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(50.dp),
            enabled = joinCode.length == 4
        ) {
            Text("Unirse a sala")
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LobbyWaitingContent(code: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "Sala creada",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Comparte este código con tu rival:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = code,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Text(
            text = "Esperando rival...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}