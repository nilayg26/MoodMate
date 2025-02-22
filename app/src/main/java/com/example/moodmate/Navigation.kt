package com.example.moodmate

interface Navigation{
    val route: String
}
object SignIn:Navigation{
    override val route: String = "SignIn"
}
object LogIn:Navigation{
    override val route: String = "LogIn"
}
object Home:Navigation{
    override val route: String = "Home"
}
object MoodTracker:Navigation {
    override val route: String = "MoodTracker"
}
object Charts:Navigation{
    override val route: String = "Charts"
}
object QuizPage:Navigation {
    override val route: String = "QuizPage"
}