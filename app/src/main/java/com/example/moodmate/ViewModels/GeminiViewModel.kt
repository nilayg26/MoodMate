package com.example.moodmate.ViewModels

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.moodmate.BuildConfig
import com.example.moodmate.Home
import com.example.moodmate.createToastMessage
import com.google.ai.client.generativeai.GenerativeModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GeminiViewModel(private val sharedPreferences: SharedPreferences) :ViewModel() {
    companion object{
        var NAME=""
    }
    var currentPage =Home.route
    var geminiStatus= mutableStateOf(GeminiState.Idle)
        private set
    var responseThought:String=""
    var responseMoodTracker:String=""
    var responseQuiz:String=""
    var testFor:String="Stress"
    init {
        val date=sharedPreferences.getString("date","")?:""
        val format= SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
        if(date.isNotEmpty()){
            val storedDate=format.parse(date)
            val currDate=format.parse(format.format(Date()))
            if (storedDate?.before(currDate) == true){
                sharedPreferences.edit().putString("date",format.format(Date())).apply()
            }
            else{
                val thought=sharedPreferences.getString("thought","")?:""
                if (thought.isNotEmpty()){
                    responseThought=thought
                    geminiStatus.value=GeminiState.ThoughtGenerated
                }
                else{
                    geminiStatus.value=GeminiState.Idle
                }
            }
        }
        else{
            sharedPreferences.edit().putString("date",format.format(Date())).apply()
        }
    }
    fun refresh(currPage:String= Home.route){
        currentPage=currPage
        geminiStatus.value=GeminiState.Idle
    }
    suspend fun getResponse(prompt:String, type:String=GeminiState.ThoughtGenerated,context: Context){
        geminiStatus.value=GeminiState.Loading
        return try {
            val apiKey = BuildConfig.API_KEY
            val generativeModel = GenerativeModel(modelName = "gemini-2.0-flash", apiKey = apiKey)
            if(type==GeminiState.ThoughtGenerated) {
                responseThought = generativeModel.generateContent(prompt = "my name is $NAME, use recent context if available, $prompt").text.toString()
                sharedPreferences.edit().putString("thought", responseThought).apply()
            }
            else if (type==GeminiState.MoodTrackerGenerated){
                responseMoodTracker = generativeModel.generateContent(prompt = "my name is $NAME, use recent context if available, $prompt").text.toString()
            }
            else if(type==GeminiState.QuizScreenGenerated){
                responseQuiz = generativeModel.generateContent(prompt = "my name is $NAME, use recent context if available, $prompt").text.toString()
            }
            geminiStatus.value=type
        } catch (t: Exception) {
            context.createToastMessage(t.message.toString())
            println("From Gemini: "+t.message+" type:"+type)
            geminiStatus.value=GeminiState.Error
        }
    }
}
object GeminiState:State{
    override var Idle: String="Idle"
    override var Error: String="Error"
    override var Loading: String="Loading"
    var ThoughtGenerated: String="SuccessThought"
    var MoodTrackerGenerated: String="SuccessMood"
    var QuizScreenGenerated: String="SuccessQuiz"
}