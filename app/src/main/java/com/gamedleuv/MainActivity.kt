package com.gamedleuv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gamedleuv.data.remote.api.RetrofitInstance
import com.gamedleuv.data.repository.AuthRepositoryImpl
import com.gamedleuv.data.repository.GameRepositoryImpl
import com.gamedleuv.data.repository.RoomRepositoryImpl
import com.gamedleuv.domain.usecase.auth.GetCurrentUserUseCase
import com.gamedleuv.domain.usecase.auth.LoginUserUseCase
import com.gamedleuv.domain.usecase.auth.RegisterUserUseCase
import com.gamedleuv.domain.usecase.auth.UploadProfilePictureUseCase
import com.gamedleuv.domain.usecase.game.GetRandomGameUseCase
import com.gamedleuv.domain.usecase.game.SearchGamesUseCase
import com.gamedleuv.ui.navigation.Routes
import com.gamedleuv.ui.screens.auth.LoginScreen
import com.gamedleuv.ui.screens.auth.RecoverPasswordScreen
import com.gamedleuv.ui.screens.auth.RegisterScreen
import com.gamedleuv.ui.screens.game.LobbyScreen
import com.gamedleuv.ui.screens.game.PvpGameScreen
import com.gamedleuv.ui.screens.game.SoloGameScreen
import com.gamedleuv.ui.screens.home.HomeScreen
import com.gamedleuv.ui.screens.profile.ProfileScreen
import com.gamedleuv.ui.theme.GamedleUVTheme
import com.gamedleuv.ui.viewmodel.AuthViewModel
import com.gamedleuv.ui.viewmodel.GameViewModel
import com.gamedleuv.ui.viewmodel.GameViewModelFactory
import com.gamedleuv.ui.viewmodel.RoomViewModel
import com.gamedleuv.ui.viewmodel.RoomViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.gamedleuv.domain.usecase.auth.ResetPasswordUserCase

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
            firestore = FirebaseFirestore.getInstance(),
        )
    }

    val authViewModel = remember {
        AuthViewModel(
            registerUser = RegisterUserUseCase(repo),
            loginUser = LoginUserUseCase(repo),
            scope = CoroutineScope(Dispatchers.Main),
            resetPassword = ResetPasswordUserCase(repo),
            getCurrentUser = GetCurrentUserUseCase(repo),
            uploadProfilePicture = UploadProfilePictureUseCase(repo),
        )
    }

    val gameRepository = remember {
        GameRepositoryImpl(RetrofitInstance.api)
    }
    val searchGamesUseCase = remember { SearchGamesUseCase(gameRepository) }
    // ← userId eliminado del factory: GameViewModel lo obtiene internamente
    // desde authRepository.getCurrentUser() en el momento de guardar la racha,
    // cuando FirebaseAuth ya tiene sesión activa y el uid nunca es "".
    val gameViewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(
            searchGamesUseCase = searchGamesUseCase,
            getRandomGameUseCase = GetRandomGameUseCase(gameRepository),
            authRepository = repo
        )
    )

    val roomRepository = remember {
        RoomRepositoryImpl(
            db = FirebaseDatabase.getInstance(),
            gameRepository = gameRepository
        )
    }

    val roomViewModel: RoomViewModel = viewModel(
        factory = RoomViewModelFactory(roomRepository, searchGamesUseCase)
    )

    val user by authViewModel.currentUser.collectAsState()
    val isInitializing by authViewModel.isInitializing.collectAsState()

    if (isInitializing) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination =
        if (user != null)
            Routes.HOME
        else
            Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
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
                onMultiClick = { navController.navigate(Routes.LOBBY) },
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

        composable(Routes.LOBBY) {
            LobbyScreen(
                authViewModel = authViewModel,
                roomViewModel = roomViewModel,
                onGameReady = { navController.navigate(Routes.PVP_GAME) }
            )
        }

        composable(Routes.PVP_GAME) {
            PvpGameScreen(
                authViewModel = authViewModel,
                roomViewModel = roomViewModel,
                onGameOver = { navController.navigate(Routes.HOME) }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController, authViewModel)
        }

        composable(Routes.RECOVER) {
            RecoverPasswordScreen(navController, authViewModel)
        }
    }
}