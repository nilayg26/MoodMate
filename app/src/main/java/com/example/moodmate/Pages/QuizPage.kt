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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.moodmate.MoodMateBottomBar
import com.example.moodmate.MoodMateTopAppBar
import com.example.moodmate.Quiz
import com.example.moodmate.ViewModels.GeminiViewModel
import com.example.moodmate.getColors
import com.example.moodmate.ui.theme.Colors
import com.example.moodmate.ui.theme.Fonts
import com.example.moodmate.ui.theme.MoodMateTheme

@Composable
fun QuizPage(navController: NavHostController, geminiViewModel: GeminiViewModel){
    val scrollState= rememberScrollState()
    val context= LocalContext.current
    val keys = listOf("Stress","Anxiety","ADHD","Depression")
    val list= mutableListOf("Test for Stress","Test for Anxiety","Test for ADHD","Test for Depression")
    MoodMateTheme {
        Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            topBar = {
                MoodMateTopAppBar("Select Assessment")
            },
            bottomBar = {
                MoodMateBottomBar(navController,1)
            }) { padding->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState).background(
                        Colors.Background
                    ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(140.dp))
                    list.forEachIndexed {idx,it->
                        Button(colors = getColors(tertiary = false),onClick = {
                            geminiViewModel.testFor= keys[idx]
                            navController.navigate(Quiz.route) }, shape = RectangleShape, modifier = Modifier.width(300.dp).clip(
                            RoundedCornerShape(10.dp),)) {
                            Text(it, fontFamily = Fonts.headlines, fontSize = 24.sp, lineHeight = 25.sp)
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                }

            }
        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun LogInPreview(){
//    QuizPage()
//}

