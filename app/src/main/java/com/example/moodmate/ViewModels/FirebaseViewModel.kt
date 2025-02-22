package com.example.moodmate.ViewModels

import androidx.lifecycle.ViewModel

sealed class AuthViewModel():ViewModel(){

}
class UserViewModel():AuthViewModel(){

}
