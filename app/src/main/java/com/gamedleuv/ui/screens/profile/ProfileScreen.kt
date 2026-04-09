package com.gamedleuv.ui.screens.profile

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
import com.gamedleuv.R
import com.gamedleuv.ui.components.AppButton
import com.gamedleuv.ui.components.VideoBg
import com.gamedleuv.ui.screens.auth.LoginScreen
import com.gamedleuv.ui.theme.GamedleUVTheme

@Composable
fun ProfileScreen() {

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

            //Foto de perfil
            //Esto es PROVISIONAL, tendrá que cambiar bastante con la lógica de recuperar la imagen subida por el user
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Username
            //Aquí también deberá cambiar por la lógica de obtener el username del usuario
            Text(
                text = "userName",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 24.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(64.dp))

            //Botón subir foto
            AppButton(
                text = "Subir Foto de Perfil",
                transparent = true,
                onClick = {//Hay que hacer la implementación de subir foto de perfil

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewProfileScreen() {
    GamedleUVTheme {
        ProfileScreen()
    }
}