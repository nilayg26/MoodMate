package com.example.moodmate.Pages

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.moodmate.MoodMateAlertDialogue
import com.example.moodmate.MoodMateButton
import com.example.moodmate.MoodMateTopAppBar
import com.example.moodmate.Quiz
import com.example.moodmate.QuizPage
import com.example.moodmate.R
import com.example.moodmate.ViewModels.GeminiState
import com.example.moodmate.ViewModels.GeminiViewModel
import com.example.moodmate.createToastMessage
import com.example.moodmate.ui.theme.Colors
import com.example.moodmate.ui.theme.MoodMateTheme
import kotlinx.coroutines.launch

data class QuizQuestion(
    val id: Int,
    val questionText: String,
    var userAnswer: MutableState<String> = mutableStateOf("")
)

val map: Map<String, SnapshotStateList<QuizQuestion>> = mapOf(
    "Stress" to mutableStateListOf(
        QuizQuestion(1, "Do you often feel overwhelmed, anxious, or irritable without a clear reason?"),
        QuizQuestion(2, "Have you noticed changes in your sleep patterns, such as difficulty falling asleep or waking up frequently?"),
        QuizQuestion(3, "Do you experience frequent headaches, muscle tension, or unexplained body aches?"),
        QuizQuestion(4, "Have you been struggling with concentration, forgetfulness, or making decisions?"),
        QuizQuestion(5, "Do you feel exhausted or unmotivated even after resting or taking breaks?")
    ),
    "ADHD" to mutableStateListOf(
        QuizQuestion(1, "Do you often struggle to focus on tasks, even when they interest you?"),
        QuizQuestion(2, "Do you frequently forget appointments, deadlines, or daily responsibilities?"),
        QuizQuestion(3, "Do you find yourself acting impulsively, like interrupting conversations or making hasty decisions?"),
        QuizQuestion(4, "Do you have difficulty sitting still, feeling restless, or constantly fidgeting?"),
        QuizQuestion(5, "Do you often start new tasks but struggle to complete them?")
    ),
    "Anxiety" to mutableStateListOf(
        QuizQuestion(1, "Do you frequently feel excessively worried or fearful, even about minor things?"),
        QuizQuestion(2, "Do you experience physical symptoms like a racing heart, sweating, or shortness of breath when anxious?"),
        QuizQuestion(3, "Do you find it hard to control or stop worrying once you start?"),
        QuizQuestion(4, "Do you avoid certain situations or activities due to fear or nervousness?"),
        QuizQuestion(5, "Do you often feel on edge, restless, or have trouble relaxing?")
    ),
    "Depression" to mutableStateListOf(
        QuizQuestion(1, "Do you often feel sad, empty, or hopeless for no clear reason?"),
        QuizQuestion(2, "Have you lost interest in activities you once enjoyed?"),
        QuizQuestion(3, "Do you feel persistently fatigued or lack energy, even after rest?"),
        QuizQuestion(4, "Do you experience changes in appetite or sleep patterns (eating/sleeping too much or too little)?"),
        QuizQuestion(5, "Do you struggle with feelings of worthlessness, guilt, or thoughts of self-harm?")
    )
)
@Composable
fun QuizScreen(navController: NavHostController, geminiViewModel: GeminiViewModel) {
    LaunchedEffect(Unit){
        geminiViewModel.refresh(Quiz.route)
    }
    var enableButton by rememberSaveable {
        mutableStateOf(true)
    }
    var enableAlertDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var response by remember {
        mutableStateOf("")
    }
    val coroutineScope= rememberCoroutineScope()
    LaunchedEffect(geminiViewModel.geminiStatus.value){
        if(geminiViewModel.currentPage==Quiz.route){
            when(geminiViewModel.geminiStatus.value){
                GeminiState.QuizScreenGenerated->{
                    response=geminiViewModel.responseQuiz
                }
                GeminiState.Loading->{
                    enableButton=false
                }
            }
        }
    }
    val testFor by remember {
        mutableStateOf(geminiViewModel.testFor)
    }
    val questions = remember {
        map[testFor]!!
    }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    MoodMateTheme {
        Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            topBar = {
                MoodMateTopAppBar("Take Test", backButton = true){
                    navController.navigate(QuizPage.route){
                        popUpTo(0){
                            inclusive=true
                        }
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (enableAlertDialog){
                    MoodMateAlertDialogue(
                        body = "Are you sure you want to submit the Test?",
                        onDismissRequest = {enableAlertDialog=it},
                        imageVector = Icons.Filled.Check
                    ) {
                        coroutineScope.launch {
                            val mapList=questions.fold(emptyMap<String,String>().toMutableMap()){
                                acc ,question->
                                acc.apply {
                                    put(question.questionText,question.userAnswer.value)
                                }
                            }
                            geminiViewModel.getResponse(context.getString(R.string.quiz)+mapList.toString()+"give your response under 60-70 words (you may use emojis), check and Assess for:$testFor",type = GeminiState.QuizScreenGenerated, context = context)
                        }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState).background(
                        Colors.Background
                    ).padding(8.dp).animateContentSize(), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if(response.isBlank()) {
                        questions.forEach { question ->
                            QuizQuestionItem(
                                question = question,
                                onAnswerChanged = { newAnswer ->
                                    question.userAnswer.value = newAnswer
                                }
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                            MoodMateButton("Submit Test", onClick = {
                                var enable = true
                                questions.forEach {
                                    if (it.userAnswer.value.isBlank()) {
                                        enable = false
                                    }
                                }
                                if (enable) {
                                    enableAlertDialog = true
                                } else {
                                    context.createToastMessage("Please answer all questions")
                                }
                            }, enabled = enableButton)
                    }
                    else{
                        QuoteBox(tod = response,animation = false,headline = "AI's Analysis")
                    }
                }
            }
        }
    }
}

@Composable
fun QuizQuestionItem(
    question: QuizQuestion,
    onAnswerChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "${question.id}. ${question.questionText}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        MoodMateTextField(
            text = question.userAnswer.value,
            icon = Icons.Filled.Edit,
            onValueChange =  onAnswerChanged,
            placeholder =  "Answer in one line",
        )
    }

}
