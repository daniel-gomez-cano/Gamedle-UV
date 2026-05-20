    package com.gamedleuv.ui.screens.home

    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import coil.compose.AsyncImage
    import com.gamedleuv.R
    import com.gamedleuv.ui.theme.GamedleUVTheme
    import com.gamedleuv.ui.components.MenuCard
    import com.gamedleuv.ui.components.VideoBg
    import com.gamedleuv.ui.navigation.Routes
    import com.gamedleuv.ui.viewmodel.AuthViewModel


    @Composable
    fun HomeScreen(
        streak: Int,
        onSoloClick: () -> Unit,
        onMultiClick: () -> Unit,
        navController: NavController,
        viewModel: AuthViewModel
    ) {
        val user by viewModel.currentUser.collectAsState()
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            VideoBg(videoResId = R.raw.fondo_auth, modifier = Modifier.fillMaxSize())

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
                    // FOTO DE PERFIL
                    if (user?.profilePictureUrl.isNullOrEmpty()) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(50))
                                .clickable { navController.navigate(Routes.PROFILE) }
                        )
                    } else {
                        val imageData = remember(user?.profilePictureUrl) {
                            user?.profilePictureUrl?.let { url ->
                                val base64 = url.substringAfter("base64,")
                                android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                            }
                        }
                        AsyncImage(
                            model = imageData,
                            contentDescription = "avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(50))
                                .clickable { navController.navigate(Routes.PROFILE) }
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = user?.username ?: "Cargando...",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

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