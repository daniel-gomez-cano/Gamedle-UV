package com.gamedleuv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gamedleuv.data.repository.AuthRepositoryImpl
import com.gamedleuv.domain.usecase.auth.LoginUserUseCase
import com.gamedleuv.domain.usecase.auth.RegisterUserUseCase
import com.gamedleuv.ui.screens.auth.LoginScreen
import com.gamedleuv.ui.screens.auth.RegisterScreen
import com.gamedleuv.ui.theme.GamedleUVTheme
import com.gamedleuv.ui.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GamedleUVTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Crear dependencias manualmente
                    val repo = AuthRepositoryImpl(
                        firebaseAuth = FirebaseAuth.getInstance(),
                        firestore = FirebaseFirestore.getInstance()
                    )
                    val registerUseCase = RegisterUserUseCase(repo)
                    val loginUseCase = LoginUserUseCase(repo)

                    val authViewModel = AuthViewModel(registerUseCase, loginUseCase)

                    LoginScreen(
                        viewModel = authViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onLoginSuccess = {
                            // Navegar a la siguiente pantalla
                        }
                    )

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GamedleUVTheme {
        Greeting("Android")
    }
}