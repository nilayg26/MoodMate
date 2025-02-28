package com.example.moodmate.Pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.moodmate.AnimationLottie
import com.example.moodmate.EmergencyContacts
import com.example.moodmate.MoodMateAlertDialogue
import com.example.moodmate.MoodMateBottomBar
import com.example.moodmate.MoodMateButton
import com.example.moodmate.MoodMateLButton
import com.example.moodmate.MoodMateOutlinedTextField
import com.example.moodmate.MoodMateTopAppBar
import com.example.moodmate.ToDoList
import com.example.moodmate.ViewModels.AnimationViewModel
import com.example.moodmate.ViewModels.AuthViewModel
import com.example.moodmate.Welcome
import com.example.moodmate.ui.theme.Colors
import com.example.moodmate.ui.theme.MoodMateTheme
import kotlinx.coroutines.launch
@Composable
fun Account(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    animationViewModel: AnimationViewModel
){
    val scrollState= rememberScrollState()
    val context= LocalContext.current
    val name = rememberSaveable {
        mutableStateOf(authViewModel.user.name)
    }
    val email = rememberSaveable {
        mutableStateOf(authViewModel.user.email)
    }
    var enableAlertDialog by remember {
        mutableStateOf(
            false
        )
    }
    val coroutineScope= rememberCoroutineScope()
    var jsonStr by rememberSaveable {
        mutableStateOf("")
    }
    LaunchedEffect(Unit){
        coroutineScope.launch {
            jsonStr=animationViewModel.getAnimation(context = context,"meditation2")
        }
    }
    MoodMateTheme {
        Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            topBar = {
                MoodMateTopAppBar("Account")
            },
            bottomBar = {
                MoodMateBottomBar(navController,3)
            }) { padding->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState).background(
                        Colors.Background
                    ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (enableAlertDialog){
                        MoodMateAlertDialogue(
                            imageVector = Icons.Filled.Warning,
                            body = "All your locally stored data will be deleted",
                            onDismissRequest = {
                                enableAlertDialog=it
                            },
                            confirmText = "Log Out"
                        ) {
                            navController.navigate(Welcome.route) {
                                authViewModel.logOut(context = context)
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    MoodMateOutlinedTextField(
                        enable = false,
                        text = name.value,
                        label = "Name",
                        password = false
                    )
                    MoodMateOutlinedTextField(
                        enable = false,
                        text = email.value,
                        label = "Email",
                        password = false
                    )
                    Spacer(Modifier.height(40.dp))
                    MoodMateButton("Emergency Contacts ", tertiary = false, icon = Icons.Filled.Phone
                    , onClick = {navController.navigate(EmergencyContacts.route){
                        launchSingleTop=true
                        } })
                    Spacer(modifier = Modifier.height(20.dp))
                    MoodMateButton("To Do List ",tertiary = false, icon = Icons.Filled.Edit, onClick = {navController.navigate(ToDoList.route){
                        launchSingleTop=true
                    } })
                    AnimationLottie(jsonStr = jsonStr)
                    MoodMateLButton(text = "Log Out"){
                        enableAlertDialog=true
                    }
                }
            }
        }
    }
}

