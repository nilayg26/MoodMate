package com.example.moodmate.Pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.moodmate.AnimationLottie
import com.example.moodmate.Home
import com.example.moodmate.MoodMateLButton
import com.example.moodmate.ViewModels.AnimationViewModel
import com.example.moodmate.ViewModels.AuthState
import com.example.moodmate.ViewModels.AuthViewModel
import com.example.moodmate.Welcome
import com.example.moodmate.ui.theme.Colors
import com.example.moodmate.ui.theme.Fonts
import com.example.moodmate.ui.theme.MoodMateTheme
import kotlinx.coroutines.launch

@Composable
fun Welcome(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    animationViewModel: AnimationViewModel
) {
    val scrollState= rememberScrollState()
    val context= LocalContext.current
    val coroutineScope= rememberCoroutineScope()
    var enableButton by remember {
        mutableStateOf(true)
    }
    var jsonStr by rememberSaveable {
        mutableStateOf("")
    }
    LaunchedEffect(Unit){
        coroutineScope.launch {
            jsonStr=animationViewModel.getAnimation(context = context,"meditation2")
        }
    }
    LaunchedEffect(authViewModel.authStatus.value) {
        when (authViewModel.authStatus.value) {
            AuthState.Authenticated -> {
                navController.navigate(Home.route){
                    popUpTo(Welcome.route){
                        inclusive=true
                    }
                }
            }
            AuthState.Loading -> {
                enableButton = false
            }
            else -> {
                enableButton = true
            }
        }
    }
    MoodMateTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { padding->
            Surface(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).background(Colors.Primary), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(20.dp))
                    AnimationLottie(size = 350,jsonStr=jsonStr)
                    Spacer(Modifier.height(30.dp))
                    Text("MoodMate\n", textAlign = TextAlign.Center, color = Color.Black, fontFamily = Fonts.headlines, fontSize = 38.sp)
                    Text("Your Mental HealthMate!", textAlign = TextAlign.Center, color = Color.Black
                    )
                    Spacer(Modifier.height(70.dp))
                    MoodMateLButton(text = "Continue with", logo =true, enable = enableButton){
                        coroutineScope.launch {
                            authViewModel.login(context =context)
                        }
                    }
                }
            }
        }
    }
}
