package com.gamedleuv.ui.screens.profile

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gamedleuv.R
import com.gamedleuv.ui.components.AppButton
import com.gamedleuv.ui.components.VideoBg
import com.gamedleuv.ui.screens.auth.LoginScreen
import com.gamedleuv.ui.theme.GamedleUVTheme
import com.gamedleuv.ui.viewmodel.AuthViewModel
import com.gamedleuv.ui.viewmodel.ProfilePictureState

@Composable
fun ProfileScreen(navController: NavController, viewModel: AuthViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val profilePictureState by viewModel.profilePictureState.collectAsState()

    // Lanzador para abrir la galería

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)

            // Comprime a 50% calidad para que quepa en Firestore, pq somos pobres :c
            val outputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 50, outputStream)
            val bytes = outputStream.toByteArray()

            viewModel.uploadProfilePicture(bytes)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        VideoBg(videoResId = R.raw.fondo_auth, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(64.dp))

            // HEADER (Icon + título)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.virus),
                    contentDescription = "Logo",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GAMEDLE",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        shadow = Shadow(
                            color = Color(0xFFB298DC), // color de la sombra
                            offset = Offset(5f, 5f),   // posición X, Y
                            blurRadius = 4f            // difuminado
                        )
                    ),
                    color = MaterialTheme.colorScheme.onBackground

                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            //Título
            Text(
                text = "TU PERFIL",
                style = MaterialTheme.typography.displayLarge.copy(),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(64.dp))

            // FOTO DE PERFIL — muestra la del usuario o el placeholder
            if (user?.profilePictureUrl.isNullOrEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                )
            } else {
                val imageData = remember(user?.profilePictureUrl) {
                    user?.profilePictureUrl?.let { url ->
                        val base64 = url.substringAfter("base64,")
                        android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                    }
                }
                if (imageData != null) {
                    AsyncImage(
                        model = imageData,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = user?.username ?: "Cargando...")

            Spacer(modifier = Modifier.height(64.dp))

            //Username
            //Aquí también deberá cambiar por la lógica de obtener el username del usuario
            Text(
                text = user?.username ?: "Cargando...",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 24.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(64.dp))

            // BOTÓN SUBIR FOTO
            AppButton(
                text = if (profilePictureState is ProfilePictureState.Loading)
                    "Subiendo..." else "Subir Foto de Perfil",
                transparent = true,
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth().height(70.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            //Botón cambiar contraseña
            AppButton(
                text = "Cambiar Contraseña",
                transparent = true,
                onClick = {
                    //Se redirige a las pantallas de recuperar contraseña
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            )
        }
    }
}