package com.gamedleuv.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamedleuv.R
import com.gamedleuv.ui.theme.GamedleUVTheme
import com.gamedleuv.ui.components.MenuCard


@Composable
fun HomeScreen(
    username: String,
    avatar: Int,
    streak: Int,
    onSoloClick: () -> Unit,
    onMultiClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Avatar + username
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = avatar),
                    contentDescription = "avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = username,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(70.dp))

            // Logo
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.virus),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(55.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "GAMEDLE",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(90.dp))

            // SOLOPLAYER
            MenuCard(
                title = "SOLOPLAYER",
                description = "Adivina el juego por su portada.\n\nTu racha: $streak",
                icon = R.drawable.videogame,
                onClick = onSoloClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            // MULTIPLAYER
            MenuCard(
                title = "MULTIPLAYER",
                description = "Gánale a los demás jugadores y sé el mejor.",
                icon = R.drawable.swords, // necesitas este drawable
                onClick = onMultiClick
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Inspirado en gamedle.wtf",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}



@Preview(showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    GamedleUVTheme(darkTheme = true) {
        HomeScreen(
            username = "kiklo187",
            avatar = R.drawable.profile,
            streak = 94,
            onSoloClick = {},
            onMultiClick = {}
        )
    }
}