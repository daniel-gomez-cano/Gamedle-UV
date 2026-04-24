package com.gamedleuv.ui.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.gamedleuv.R
import com.gamedleuv.ui.components.AppPasswordField
import com.gamedleuv.ui.components.VideoBg
import com.gamedleuv.ui.viewmodel.AuthUiState
import com.gamedleuv.ui.viewmodel.AuthViewModel
import com.gamedleuv.ui.navigation.Routes

@Composable
fun RegisterScreen(
    navController: NavController? = null,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel? = null
) {

    val uiState by (viewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(AuthUiState.Idle) })

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            navController?.navigate(Routes.LOGIN) {
                popUpTo(Routes.REGISTER) {
                    inclusive = true
                }
                launchSingleTop = true
            }
            viewModel?.resetState()
        }
    }


    Box(
        modifier = modifier
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Registrarse",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Inputs
                Text(
                    text = "Usuario",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                AppTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Ingresar usuario",

                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Correo Electronico",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Ingresar correo"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                AppPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Ingresar contraseña",
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Confirmar contraseña",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                AppTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirmar contraseña"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Boton componente
                AppButton(
                    text = "Continuar",
                    onClick = {
                        if (password == confirmPassword) {
                            validationError = null
                            viewModel?.register(email, password, username)
                        } else {
                            validationError = "Las contraseñas no coinciden"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                )
                if (validationError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(validationError.orEmpty(), color = Color.Red)
                }
                when (uiState) {
                    is AuthUiState.Idle -> {}
                    is AuthUiState.Loading -> CircularProgressIndicator()
                    is AuthUiState.Success -> Text((uiState as AuthUiState.Success).msg)
                    is AuthUiState.Error -> Text((uiState as AuthUiState.Error).error, color = Color.Red)
                }



                Spacer(modifier = Modifier.height(16.dp))

                // Fotter
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Ya tiene cuenta? ",
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Inicie Sesión",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable {
                            navController?.navigate(Routes.LOGIN) {
                                popUpTo(Routes.REGISTER) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }

}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewRegisterScreen() {
    GamedleUVTheme {
        RegisterScreen()
    }
}


