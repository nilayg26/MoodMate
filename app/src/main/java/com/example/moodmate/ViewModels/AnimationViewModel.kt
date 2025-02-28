package com.example.moodmate.ViewModels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.moodmate.R
import com.example.moodmate.createToastMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import java.net.UnknownHostException
class AnimationViewModel(private val sharedPreferences: SharedPreferences):ViewModel() {
    private val client = HttpClient(Android)
    private var meditation2 = sharedPreferences.getString("meditation2", "") ?: ""
    private var yoga = sharedPreferences.getString("yoga", "") ?: ""
    private var mood = sharedPreferences.getString("mood", "") ?: ""
    suspend fun getAnimation(context: Context, string: String): String {
        try {
            if (string == "meditation2") {
                if (meditation2.isBlank()) {
                    val responseMeditation2 = client.get(context.getString(R.string.meditation2))
                    meditation2 = responseMeditation2.body<String>()
                    sharedPreferences.edit().putString("meditation2", meditation2).apply()
                    println("Got it meditation")
                }
                return meditation2
            }
            if (string == "yoga") {
                if (yoga.isBlank()) {
                    val responseYoga = client.get(context.getString(R.string.yoga))
                    yoga = responseYoga.body<String>()
                    sharedPreferences.edit().putString("yoga", yoga).apply()
                    println("Got it yoga")
                }
                return yoga
            }
            if (string == "mood") {
                if (mood.isBlank()) {
                    val responseMood = client.get(context.getString(R.string.mood))
                    mood = responseMood.body<String>()
                    sharedPreferences.edit().putString("mood", mood).apply()
                    println("Got it mood")
                }
                return mood
            }
        }
        catch (e:UnknownHostException){
            context.createToastMessage("Cannot connect to internet")
        }
        catch (e:Exception){
            context.createToastMessage(e.message.toString())
            println("Error from AnimationViewModel: "+e.message.toString())
        }
        return ""
    }
}