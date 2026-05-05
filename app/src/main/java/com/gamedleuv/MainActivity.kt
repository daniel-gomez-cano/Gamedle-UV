package com.gamedleuv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gamedleuv.data.repository.AuthRepositoryImpl
import com.gamedleuv.domain.usecase.auth.LoginUserUseCase
import com.gamedleuv.domain.usecase.auth.RegisterUserUseCase
import com.gamedleuv.ui.screens.auth.LoginScreen
import com.gamedleuv.ui.navigation.Routes
import com.gamedleuv.ui.screens.auth.RegisterScreen
import com.gamedleuv.ui.theme.GamedleUVTheme
import com.gamedleuv.ui.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.gamedleuv.ui.screens.auth.GetCodeScreen
import com.gamedleuv.ui.screens.auth.NewPasswordScreen
import com.gamedleuv.ui.screens.auth.RecoverPasswordScreen
import com.gamedleuv.ui.screens.home.HomeScreen
import com.gamedleuv.ui.screens.profile.ProfileScreen
import com.gamedleuv.domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GamedleUVTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val repo = remember {
        AuthRepositoryImpl(
            firebaseAuth = FirebaseAuth.getInstance(),
            firestore = FirebaseFirestore.getInstance()
        )
    }
    val registerUseCase = remember { RegisterUserUseCase(repo) }
    val loginUseCase = remember { LoginUserUseCase(repo) }

    val authViewModel = remember {
        AuthViewModel(registerUseCase, loginUseCase, CoroutineScope(Dispatchers.Main))
    }
    val user by authViewModel.currentUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }

        composable(Routes.REGISTER){
            RegisterScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }

        composable(Routes.HOME){ // En casos donde la pantalla requiere de datos para funcionar, se deben asignar todos ellos (este es de prueba, luego toca poner que capture los datos del usuario de la bd)
            HomeScreen(
                streak = user?.currentStreak ?: 0,
                onSoloClick = {},
                onMultiClick = {},
                navController,
                authViewModel
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController, authViewModel)
        }

        composable(Routes.RECOVER){
            RecoverPasswordScreen(navController)
        }

        composable(Routes.GETCODE){
            GetCodeScreen(navController)
        }

        composable(Routes.NEWPASSWORD){
            NewPasswordScreen(navController)
        }
    }
}