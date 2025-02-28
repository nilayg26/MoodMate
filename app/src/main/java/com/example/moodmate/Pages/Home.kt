package com.example.moodmate.Pages
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.moodmate.AnimationLottie
import com.example.moodmate.Home
import com.example.moodmate.MoodMateBottomBar
import com.example.moodmate.MoodMateButton
import com.example.moodmate.MoodMateTopAppBar
import com.example.moodmate.MoodTracker
import com.example.moodmate.R
import com.example.moodmate.ViewModels.AnimationViewModel
import com.example.moodmate.ViewModels.AuthViewModel
import com.example.moodmate.ViewModels.GeminiState
import com.example.moodmate.ViewModels.GeminiViewModel
import com.example.moodmate.ui.theme.Colors
import com.example.moodmate.ui.theme.MoodMateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    geminiViewModel: GeminiViewModel,
    animationViewModel: AnimationViewModel
) {
    var tod by rememberSaveable{
        mutableStateOf(geminiViewModel.responseThought)
    }
    var isRefreshing by rememberSaveable {
        mutableStateOf(false)
    }
    val pullToRefreshState= rememberPullToRefreshState()
    val scrollState= rememberScrollState()
    val context= LocalContext.current
    var jsonStr by rememberSaveable {
        mutableStateOf("")
    }
    LaunchedEffect(Unit){
        jsonStr=animationViewModel.getAnimation(context = context,"yoga")
    }
    if(isRefreshing){
        LaunchedEffect (Unit){
            tod=""
            geminiViewModel.refresh()
            geminiViewModel.getResponse(prompt = context.getString(R.string.though_of_the_day), context = context)
        }
    }
    LaunchedEffect(geminiViewModel.geminiStatus.value) {
        if (geminiViewModel.currentPage==Home.route) {
            when (geminiViewModel.geminiStatus.value) {
                GeminiState.Idle -> {
                    isRefreshing=true
                }
                GeminiState.ThoughtGenerated -> {
                    tod = geminiViewModel.responseThought
                    isRefreshing = false
                }
                else ->{
                    if (geminiViewModel.responseThought.isNotBlank()) {
                        tod = geminiViewModel.responseThought
                    }
                }
            }
        }
    }
    MoodMateTheme {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                MoodMateTopAppBar("Hello ${getName(authViewModel.user.name)}!")
            },
            bottomBar = {
                MoodMateBottomBar(navController,0)
            }) { padding->
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                modifier = Modifier,
                contentAlignment = Alignment.TopCenter,
            ){
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState).background(
                        Colors.Background
                    ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(30.dp))
                    QuoteBox(tod, jsonStr = jsonStr)
                    Spacer(Modifier.height(10.dp))
                    MoodMateButton(text = "Track Your Mood", onClick = {
                        navController.navigate(MoodTracker.route){
                            launchSingleTop=true
                        }
                    })
                }
            }
            }
        }
    }
}

@Composable
fun QuoteBox(tod:String,animation:Boolean=true,headline:String="Your Personalised\n Thought of the day!",jsonStr:String=""){
    Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedVisibility(tod.isNotEmpty()) {
            Text(
                text = headline,
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
        }
        Spacer(Modifier.height(10.dp))
        Box(modifier = Modifier.padding(start = 10.dp, end = 10.dp).fillMaxWidth()){
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement =Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
               AnimatedVisibility(tod.isNotEmpty()) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                            .background(Colors.Secondary)
                    ) {
                        Text(
                            tod,
                            color = Color.Black,
                            modifier = Modifier.padding(5.dp),
                            lineHeight = 30.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                if(animation){AnimationLottie(jsonStr =jsonStr)}
            }
        }
    }
}
private fun getName(fullName:String):String{
    val split= fullName.split(" ")
    return when(split.size){
        0-> ""
        in 1..2 -> split[0]
        else -> split[0]+" "+split[1]
    }
}