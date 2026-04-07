package com.gamedleuv.ui.screens.game

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gamedleuv.R
import com.gamedleuv.ui.components.AppButton
import com.gamedleuv.ui.components.DropdownField
import com.gamedleuv.ui.components.HeartsRow
import com.gamedleuv.ui.components.ProfileButton
import com.gamedleuv.ui.theme.GamedleUVTheme

@Composable
fun SoloGameScreen(
    username: String,
    racha: Int,
    actual_lives: Int,
    maxLives: Int,
    listGames: List<String>,
    selectedOption: String
) {

    var selectedOption by remember { mutableStateOf(selectedOption) }

    // Fondo negro que ocupa toda la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {

            // ── HEADER: ícono + título GAMEDLE ──────────────────────────────
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

            // ── FILA: avatar + nombre de usuario | corazones ────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar pequeño + nombre de usuario
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Placeholder de avatar circular

                    Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "avatar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(36.dp)

                    )


                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = username,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Fila de corazones
                HeartsRow(
                    total = maxLives,
                    filled = actual_lives,
                    size = 30
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── IMAGEN DEL JUEGO (~50% de la pantalla) ──────────────────────
            // weight(1f) hace que la imagen tome todo el espacio libre disponible
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "game image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f) // Ajusta este valor (0.25 a 0.70) según prefieras
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)) // Opcional: para ver límites
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── FILA: botón Saltar | botón Pista | Racha ────────────────────
            // Los botones y la racha van en la misma fila horizontal
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppButton(
                    style = MaterialTheme.typography.labelMedium,
                    text = "Saltar",
                    onClick = {},
                    modifier = Modifier
                        .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50.dp))
                        .weight(1f)
                        .height(50.dp)



                )

                AppButton(
                    style = MaterialTheme.typography.labelMedium,
                    text = "Pista",
                    onClick = {},
                    modifier = Modifier
                        .border(4.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(50.dp))
                        .weight(1f)
                        .height(50.dp)
                )

                // Racha

                Text(
                    text = "Racha:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "$racha",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── CAMPO DE BÚSQUEDA + BOTÓN FLECHA ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Dropdown ocupa todo el ancho menos el botón de flecha
                DropdownField(
                    options = listGames,
                    selected = selectedOption,
                    onSelectedChange = { selectedOption = it },
                    modifier = Modifier.weight(1f)
                )

                ProfileButton(
                    img = R.drawable.arrow,
                    transparent = true,
                    iconSize = 24.dp,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = {},
                    modifier = Modifier
                        .size(56.dp)


                )
            }

        }
    }
}


@Preview(
    name = "Solo Game - Dark Mode",
    showSystemUi = true
)
@Composable
fun PreviewSoloGameScreen() {
    GamedleUVTheme(darkTheme = true) {
        SoloGameScreen(
            username = "Kano065",
            racha = 96,
            actual_lives = 2,
            maxLives = 5,
            listGames = listOf(
                "A Dance of Fire and Ice",
                "A Difficult Game About Climbing",
                "A Game About Digging A Hole",
                "A Story About My Uncle",
                "Abiotic Factor"
            ),
            selectedOption = "A",
        )
    }
}