package com.gamedleuv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gamedleuv.data.remote.api.RetrofitInstance
import com.gamedleuv.data.repository.AuthRepositoryImpl
import com.gamedleuv.data.repository.GameRepositoryImpl
import com.gamedleuv.domain.usecase.auth.LoginUserUseCase
import com.gamedleuv.domain.usecase.auth.RegisterUserUseCase
import com.gamedleuv.domain.usecase.game.GetRandomGameUseCase
import com.gamedleuv.domain.usecase.game.SearchGamesUseCase
import com.gamedleuv.ui.navigation.Routes
import com.gamedleuv.ui.screens.auth.GetCodeScreen
import com.gamedleuv.ui.screens.auth.LoginScreen
import com.gamedleuv.ui.screens.auth.NewPasswordScreen
import com.gamedleuv.ui.screens.auth.RecoverPasswordScreen
import com.gamedleuv.ui.screens.auth.RegisterScreen
import com.gamedleuv.ui.screens.game.SoloGameScreen
import com.gamedleuv.ui.screens.home.HomeScreen
import com.gamedleuv.ui.screens.profile.ProfileScreen
import com.gamedleuv.ui.theme.GamedleUVTheme
import com.gamedleuv.ui.viewmodel.AuthViewModel
import com.gamedleuv.ui.viewmodel.GameViewModel
import com.gamedleuv.ui.viewmodel.GameViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    val authViewModel = remember {
        AuthViewModel(
            registerUser = RegisterUserUseCase(repo),
            loginUser = LoginUserUseCase(repo),
            scope = CoroutineScope(Dispatchers.Main)
        )
    }

    val gameRepository = remember {
        GameRepositoryImpl(RetrofitInstance.api)
    }

    // ← userId eliminado del factory: GameViewModel lo obtiene internamente
    // desde authRepository.getCurrentUser() en el momento de guardar la racha,
    // cuando FirebaseAuth ya tiene sesión activa y el uid nunca es "".
    val gameViewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(
            searchGamesUseCase = SearchGamesUseCase(gameRepository),
            getRandomGameUseCase = GetRandomGameUseCase(gameRepository),
            authRepository = repo
        )
    )

    val user by authViewModel.currentUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }

        composable(Routes.HOME) {
            HomeScreen(
                streak = user?.currentStreak ?: 0,
                onSoloClick = { navController.navigate(Routes.SOLO_GAME) },
                onMultiClick = {},
                navController = navController,
                viewModel = authViewModel
            )
        }

        composable(Routes.SOLO_GAME) {
            SoloGameScreen(
                authViewModel = authViewModel,
                gameViewModel = gameViewModel
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController, authViewModel)
        }

        composable(Routes.RECOVER) {
            RecoverPasswordScreen(navController)
        }

        composable(Routes.GETCODE) {
            GetCodeScreen(navController)
        }

        composable(Routes.NEWPASSWORD) {
            NewPasswordScreen(navController)
        }
    }
}