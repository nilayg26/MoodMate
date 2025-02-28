package com.example.moodmate

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moodmate.Pages.Account
import com.example.moodmate.Pages.Charts
import com.example.moodmate.Pages.EmergencyContacts
import com.example.moodmate.Pages.Home
import com.example.moodmate.Pages.MoodTracker
import com.example.moodmate.Pages.QuizPage
import com.example.moodmate.Pages.QuizScreen
import com.example.moodmate.Pages.TodoListScreen
import com.example.moodmate.Pages.Welcome
import com.example.moodmate.ViewModels.AnimationViewModel
import com.example.moodmate.ViewModels.AuthViewModel
import com.example.moodmate.ViewModels.GeminiViewModel
import com.example.moodmate.ViewModels.InternalDataBaseViewModel
import com.example.moodmate.ui.theme.Colors
import com.example.moodmate.ui.theme.MoodMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences= this.getSharedPreferences("MoodMate",Context.MODE_PRIVATE)
        val animationViewModel by viewModels<AnimationViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory{
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return (AnimationViewModel(sharedPreferences) as T)
                    }
                }
            }
        )
        val chartViewModel by viewModels<InternalDataBaseViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory{
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return (InternalDataBaseViewModel(sharedPreferences,this@MainActivity) as T)
                    }
                }
            }
        )
        val geminiViewModel by viewModels<GeminiViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory{
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return (GeminiViewModel(sharedPreferences) as T)
                    }
                }
            }
        )
        val authViewModel :AuthViewModel by viewModels<AuthViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory{
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return (AuthViewModel(sharedPreferences) as T)
                    }
                }
            }
        )
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(scrim = Colors.Background.toArgb(),
                darkScrim = Colors.Background.toArgb()),
            navigationBarStyle = SystemBarStyle.light(scrim = Colors.Background.toArgb(),
                darkScrim = Colors.Background.toArgb()),
        )
        setContent {
            MoodMateTheme {
                Navigation(authViewModel,geminiViewModel,animationViewModel,chartViewModel)
            }
        }
    }
}
@Composable
fun Navigation(
    authViewModel: AuthViewModel,
    geminiViewModel: GeminiViewModel,
    animationViewModel: AnimationViewModel,
    internalDatabaseViewModel: InternalDataBaseViewModel
) {
    val navController= rememberNavController()
    LaunchedEffect(authViewModel.crashApp.value){
         authViewModel.suspendApp()
    }
    if (!authViewModel.crashApp.value) {
        NavHost(
            navController = navController,
            startDestination = if (!authViewModel.loginStatus.value) {
                Welcome.route
            } else {
                Home.route
            }
        ) {
            composable(Welcome.route) {
                Welcome(navController, authViewModel, animationViewModel)
            }
            composable(Home.route) {
                Home(navController, authViewModel, geminiViewModel, animationViewModel)
            }
            composable(Account.route) {
                Account(navController, authViewModel, animationViewModel)
            }
            composable(QuizPage.route) {
                QuizPage(navController, geminiViewModel)
            }
            composable(
                Quiz.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { it }) }) {
                QuizScreen(navController, geminiViewModel)
            }
            composable(Charts.route) {
                Charts(navController, internalDatabaseViewModel)
            }
            composable(MoodTracker.route) {
                MoodTracker(
                    navController,
                    geminiViewModel,
                    animationViewModel,
                    internalDatabaseViewModel
                )
            }
            composable(
                ToDoList.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { it }) }) {
                TodoListScreen(navController, internalDatabaseViewModel)
            }
            composable(
                EmergencyContacts.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { it }) }) {
                EmergencyContacts(navController, internalDatabaseViewModel)
            }
        }
    }
    else{
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Access Denied by Admin", fontSize = 50.sp, color = Color.Black)
        }
    }
}