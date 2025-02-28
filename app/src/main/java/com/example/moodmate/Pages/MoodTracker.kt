package com.example.moodmate.Pages
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.moodmate.AnimationLottie
import com.example.moodmate.MoodMateAlertDialogue
import com.example.moodmate.MoodMateButton
import com.example.moodmate.MoodMateTopAppBar
import com.example.moodmate.MoodTracker
import com.example.moodmate.R
import com.example.moodmate.ViewModels.AnimationViewModel
import com.example.moodmate.ViewModels.GeminiState
import com.example.moodmate.ViewModels.GeminiViewModel
import com.example.moodmate.ViewModels.InternalDataBaseViewModel
import com.example.moodmate.ui.theme.Colors
import com.example.moodmate.ui.theme.Fonts
import com.example.moodmate.ui.theme.MoodMateTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MoodTracker(
    navController: NavHostController,
    geminiViewModel: GeminiViewModel,
    animationViewModel: AnimationViewModel,
    chartViewModel: InternalDataBaseViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var moodInput by rememberSaveable{ mutableStateOf("") }
    val currentDate = remember {
        SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
    }
    val coroutineScope= rememberCoroutineScope()
    var enableTextField by remember {
        mutableStateOf(true)
    }
    var enableButton by remember {
        mutableStateOf(true)
    }
    var enableDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var response by rememberSaveable {
        mutableStateOf("")
    }
    var jsonStr by rememberSaveable {
        mutableStateOf("")
    }
    LaunchedEffect(response.isNotBlank()) {
        if (response.isNotBlank()){
         chartViewModel.setChartData(response)
        }
    }
    LaunchedEffect(Unit){
        coroutineScope.launch {
            jsonStr=animationViewModel.getAnimation(context = context,"mood")
        }
    }
    LaunchedEffect(Unit) {
        geminiViewModel.refresh(currPage = MoodTracker.route)
    }
    LaunchedEffect(geminiViewModel.geminiStatus.value){
        if (geminiViewModel.currentPage==MoodTracker.route) {
            when (geminiViewModel.geminiStatus.value) {
                GeminiState.MoodTrackerGenerated -> {
                    response = geminiViewModel.responseMoodTracker
                    enableButton = false
                    enableTextField=false
                }
                GeminiState.Loading -> {
                    enableButton = false
                }
                else -> {

                }
            }
        }
    }
    MoodMateTheme {
        Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            topBar = {
                MoodMateTopAppBar("Mood Tracker", backButton = true){
                    if (navController.currentBackStackEntry != null){
                        navController.popBackStack()
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState).background(
                        Colors.Background
                    ).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp).animateContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (enableDialog) {
                            MoodMateAlertDialogue(
                                body = "Are you sure to submit your mood?\nThis data will be recorded to generate personalised Stats",
                                imageVector = Icons.Filled.Check,
                                onDismissRequest = {enableDialog=it}
                            ) {
                                coroutineScope.launch {
                                    geminiViewModel.getResponse(
                                        type = GeminiState.MoodTrackerGenerated,
                                        prompt = context.getString(R.string.mood_tracker) + moodInput,
                                        context = context
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Daily Mood Tracker",
                            fontSize = 24.sp,
                            fontFamily = Fonts.headlines,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Colors.Secondary,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = currentDate,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "How are you feeling today?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                       if(enableTextField) {
                           MoodMateTextField(
                               text = moodInput,
                               onValueChange = { moodInput = it },
                               icon = Icons.Filled.Create,
                               placeholder = "Your mood..."
                           )
                       }
                        else{
                           Box(
                               modifier = Modifier.clip(RoundedCornerShape(10.dp))
                                   .background(Colors.Background)
                           ) {
                               if(response.isNotBlank()){
                                   QuoteBox(tod=response, headline = "AI's response", animation = false)
                               }
                           }
                        }
                        AnimationLottie(jsonStr = jsonStr)
                        MoodMateButton(enabled = moodInput.isNotBlank()&& enableButton,
                            text = "Submit", onClick = {
                                enableDialog=true
                            }
                        )
                    }
                }
            }
        }
    }
}


