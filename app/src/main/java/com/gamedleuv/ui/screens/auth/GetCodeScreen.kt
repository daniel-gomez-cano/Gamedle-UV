package com.gamedleuv.ui.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.gamedleuv.ui.components.AppButton
import com.gamedleuv.ui.components.AppTextField
import com.gamedleuv.ui.theme.GamedleUVTheme
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.gamedleuv.R
import com.gamedleuv.ui.components.VideoBg
import com.gamedleuv.ui.navigation.Routes

@Composable
fun GetCodeScreen(navController: NavController) {
    // Revisar esta lógica de recibir el código por correo.
    var emailCode by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        VideoBg(videoResId = R.raw.fondo_main, modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.secondary,
                    RoundedCornerShape(30.dp)
                )
                .padding(24.dp)
                .padding(top = 34.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Icono y titulo
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.virus),
                        contentDescription = "Logo",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GAMEDLE",
                        style = MaterialTheme.typography.displayLarge.copy(
                            shadow = Shadow(
                                color = Color(0xFFB298DC), // color de la sombra
                                offset = Offset(5f, 5f),   // posición X, Y
                                blurRadius = 4f            // difuminado
                            )
                        ),
                        color = MaterialTheme.colorScheme.onBackground

                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "Ingresa el Código",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "Se ha enviado un código de acceso a su correo, por favor digitelo aquí.",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(36.dp))
                // Revisar esta lógica de recibir el código por correo.
                AppTextField(
                    value = emailCode,
                    onValueChange = { emailCode = it },
                    placeholder = "Ingrese el código"
                )

                Spacer(modifier = Modifier.height(36.dp))


                // Boton componente
                AppButton(
                    text = "Recuperar",
                    onClick = {
                        navController.navigate(Routes.NEWPASSWORD)
                        // lógica registro
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Footer
                // No está en el Figma, pero es importante tener esta acción para el usuario.
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Ya tiene cuenta? ",
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Inicie sesión",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable {
                            // Aqui va la ventana a la que dirige
                        }
                    )
                }
            }
        }
    }
}