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
    override val route: String = "Stats"
}
object QuizPage:Navigation {
    override val route: String = "QuizPage"
}
object Quiz:Navigation {
    override val route: String = "Quiz"
}
object Account:Navigation {
    override val route: String = "Account"
}
object EmergencyContacts:Navigation {
    override val route: String = "EmergencyContacts"
}
object Welcome:Navigation {
    override val route: String = "Welcome"
}
object ToDoList:Navigation {
    override val route: String = "ToDoList"
}
object BottomBar:Navigation {
    override val route: String = "BottomBar"
}