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
fun PvpGameScreen(
    username1: String,
    avatar1: Int,
    lives1: Int,

    username2: String,
    avatar2: Int,
    lives2: Int,

    maxLives: Int,
    timer: Int,
    gameImage: Int,
    listGames: List<String>,
    selectedOption: String
){

    var selectedOption by remember { mutableStateOf(selectedOption) }


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

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center

            ) {
                //Icono de virus
                Icon(
                    painter = painterResource(id = R.drawable.virus),
                    contentDescription = "logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                //Titulo GAMEDLE
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

            // Fila general debajo del titulo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Jugador 1
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = avatar1),
                        contentDescription = "avatar1",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = username1,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // TIMER en el centro
                Text(
                    text = "${timer}s",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                // Jugador 2
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = username2,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        painter = painterResource(id = avatar2),
                        contentDescription = "avatar2",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HeartsRow(
                    total = maxLives,
                    filled = lives1,
                    size = 24
                )

                HeartsRow(
                    total = maxLives,
                    filled = lives2,
                    size = 24
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            // IMAGEN DEL JUEGO
            Image(
                painter = painterResource(id = gameImage),
                contentDescription = "game image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))



            Spacer(modifier = Modifier.height(10.dp))

            // Fila del campo de busqueda y la flecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //Campo de busqueda
                DropdownField(
                    options = listGames,
                    selected = selectedOption,
                    onSelectedChange = { selectedOption = it },
                    modifier = Modifier.weight(1f)
                )

                //Boton flecha
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
    name = "PvP - Dark Mode",
    showSystemUi = true
)
@Composable
fun PreviewPvpGameScreen() {
    GamedleUVTheme(darkTheme = true) {
            PvpGameScreen(
                username1 = "elrubiusOmg",
                avatar1 = R.drawable.profile,
                lives1 = 2,

                username2 = "kiklo187",
                avatar2 = R.drawable.profile,
                lives2 = 1,

                maxLives = 5,
                timer = 27,
                gameImage = R.drawable.ic_launcher_background,
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