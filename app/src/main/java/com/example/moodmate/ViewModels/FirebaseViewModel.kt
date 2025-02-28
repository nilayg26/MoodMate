package com.example.moodmate.ViewModels
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import com.example.moodmate.R
import com.example.moodmate.createToastMessage
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

data class User(
    var name:String,
    var email: String,
    var picUrl:String?=""
)
class AuthViewModel(private val sharedPreferences: SharedPreferences):ViewModel() {
    val user:User=User("","")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var authStatus = mutableStateOf("")
        private set
    var loginStatus= mutableStateOf(false)
        private set
    init{
        val loginStatus=sharedPreferences.getBoolean("loginStatus",false)
        if (loginStatus){
            GeminiViewModel.NAME=user.name
            user.name=sharedPreferences.getString("name","")?:""
            user.email=sharedPreferences.getString("email","")?:""
            user.picUrl=sharedPreferences.getString("picUrl","")?:""
            this.loginStatus.value=true
            authStatus.value=AuthState.Authenticated
        }
        else{
            authStatus.value=AuthState.Unauthenticated
        }
    }
    suspend fun login(context: Context) {
        authStatus.value = AuthState.Loading
        val request = getRequest(context = context)
        val credentialManager = CredentialManager.create(context = context)
        try {
            val result = credentialManager.getCredential(request = request, context = context)
            when (result.credential) {
                is CustomCredential -> {
                    if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(
                            result.credential.data
                        )
                        val googleIdTokenId = googleIdTokenCredential.idToken
                        val authCredential = GoogleAuthProvider.getCredential(googleIdTokenId, null)
                        auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                user.name= auth.currentUser?.displayName?:"No name can be found"
                                GeminiViewModel.NAME=user.name
                                user.email= auth.currentUser?.email?:"No email can be found"
                                user.picUrl= (auth.currentUser?.photoUrl?:"null").toString()
                                sharedPreferences.edit()
                                    .putBoolean("loginStatus",true)
                                    .putString("name",user.name)
                                    .putString("email",user.email)
                                    .putString("picUrl",user.picUrl.toString())
                                    .apply()
                                authStatus.value = AuthState.Authenticated
                            } else {
                                val msg=task.exception?.message ?: "Something went wrong"
                                println("login():$msg")
                                context.createToastMessage(msg)
                                authStatus.value = AuthState.Error
                            }
                        }
                    } else {
                        context.createToastMessage("Try Again!")
                        authStatus.value= AuthState.Unauthenticated
                    }
                }
            }

        }
        catch (e:Exception) {
            context.createToastMessage("Could not get to your Google Account")
            println("Error from LogIn Function: "+e.message.toString())
            authStatus.value= AuthState.Error
        }
    }
    private fun getRequest(context: Context): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .build()
        return (GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build())
    }
    fun logOut(
        context: Context,
    ) {
        sharedPreferences.edit().clear().putInt("count",-1).apply()
        auth.signOut()
        context.createToastMessage("Log out successful")
        authStatus.value = AuthState.Unauthenticated
    }
}
interface State{
    var Idle:String
    var Error:String
    var Loading:String
}
object AuthState :State{
    override var Idle="idle"
     var Authenticated="authenticated"
     var Unauthenticated="unauthenticated"
    override var Error="Error"
    override var Loading="Loading"
}

