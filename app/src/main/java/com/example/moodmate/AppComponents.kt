package com.example.moodmate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.moodmate.ui.theme.Colors
import com.example.moodmate.ui.theme.Fonts
import com.example.moodmate.ui.theme.Typography

@Composable
fun AnimationLottie(size: Int = 200, jsonStr: String = ""){
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.JsonString(
            jsonStr
        )
    )
    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        isPlaying = true,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition =preloaderLottieComposition,
        progress = preloaderProgress,
        modifier = Modifier.size(size.dp)
    )
}
@Composable
fun MoodMateLButton(
    enable: Boolean=true,
    size: Int = 200,
    text: String,
    outline: Boolean = false,
    logo: Boolean=false ,
    onClick: () -> Unit = {}
){
    if (!outline) {
        Button(enabled = enable,onClick = onClick, modifier = Modifier.width(size.dp), colors = getColors()) {
            Text(text = text, fontFamily = Fonts.headlines)
            if (logo){
                Image(modifier = Modifier
                    .padding(start = 10.dp)
                    .size(20.dp)
                    .clip(CircleShape),painter = painterResource(R.drawable.google_logo), contentDescription = "")
            }
        }
    }
    else{
        OutlinedButton(onClick = onClick, modifier = Modifier.width(size.dp)) {
            Text(text = text, fontFamily = Fonts.headlines, color = Color.Black)
        }
    }
}
fun getColors(tertiary:Boolean=true): ButtonColors {
    return (
            ButtonColors(
                containerColor = if(tertiary)Colors.Tertiary else Colors.Secondary,
                contentColor = if(tertiary)Color.White else Color.Black,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            )
            )
}
@Composable
fun MoodMateOutlinedTextField(enable: Boolean =true, text: String, password:Boolean=true, label: String="", lamda: (String) ->Unit={}){
    var passwordVisible by remember {
        mutableStateOf(true)
    }
    OutlinedTextField(value = text, enabled = enable,onValueChange ={newVal-> lamda(newVal)}, label = { Text(
        text = label, fontSize = 14.sp, fontFamily = Fonts.paragraph
    )}, textStyle = Typography.labelSmall,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(disabledLabelColor = Color.DarkGray,disabledTextColor = Color.DarkGray, disabledBorderColor = Color.DarkGray, disabledTrailingIconColor = Color.DarkGray,unfocusedLabelColor = Color.DarkGray,unfocusedBorderColor = Color.DarkGray,unfocusedTextColor = Color.DarkGray,unfocusedTrailingIconColor = Color.DarkGray,focusedTrailingIconColor = Color.Black, focusedLabelColor = Color.Black, focusedTextColor = Color.Black,  focusedBorderColor = Color.Black),
        shape = RoundedCornerShape(20.dp), modifier = Modifier.padding(top = 10.dp),
        visualTransformation =
        if (passwordVisible && password) PasswordVisualTransformation() else VisualTransformation.None ,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if(!password){
                Icons.Filled.AccountBox}else if (passwordVisible)
                Icons.Filled.Favorite
            else Icons.Filled.FavoriteBorder
            val description = if (passwordVisible) "Hide password" else "Show password"
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = description)
            }
        }
    )
}


object BottomBarProps{
    val list= mutableListOf("Home","QuizPage","Stats","Account")
    val mapFilled= mutableMapOf("Home" to Icons.Filled.Home,"QuizPage" to Icons.Filled.Create,"Stats" to Icons.Filled.Info,"Account" to Icons.Filled.AccountCircle)
    val mapOutlined= mutableMapOf("Home" to Icons.Outlined.Home,"QuizPage" to Icons.Outlined.Create,"Stats" to Icons.Outlined.Info,"Account" to Icons.Outlined.AccountCircle)
}
@Composable
fun MoodMateBottomBar(navController: NavHostController, selectedIndex:Int) {
    NavigationBar(containerColor = Colors.Background) {
        BottomBarProps.list.forEachIndexed { idx, it ->
            NavigationBarItem(onClick = {
                navController.navigate(BottomBarProps.list[idx]) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }, selected = selectedIndex == idx, label = { Text(it) }, icon = {
                if (selectedIndex == idx) {
                    BottomBarProps.mapFilled[it]?.let { it1 ->
                        Icon(
                            contentDescription = it,
                            imageVector = it1
                        )
                    }
                } else {
                    BottomBarProps.mapOutlined[it]?.let { it1 ->
                        Icon(
                            contentDescription = it,
                            imageVector = it1
                        )
                    }
                }
            }
            )
        }
    }
}

@Composable
fun MoodMateButton(text:String, onClick:()-> Unit={}, enabled:Boolean=true, tertiary:Boolean=true, icon: ImageVector?=null){
    Button(
        enabled = enabled,
        colors = getColors(tertiary),
        onClick = onClick,
        shape = RectangleShape,
        modifier = Modifier
            .width(316.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Text(text, fontFamily = Fonts.headlines, fontSize = 24.sp)
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = "")
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodMateTopAppBar(text: String, backButton: Boolean = false,onBackButton:()->Unit={}){
    TopAppBar(scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        modifier = Modifier.background(Colors.Background),
        colors = TopAppBarColors(
            containerColor = Colors.Background,
            scrolledContainerColor = Colors.Background,
            navigationIconContentColor = Colors.Background,
            titleContentColor = Color.Black,
            actionIconContentColor = Colors.Background
        ),
        title = {
            Text(text = text, fontFamily = Fonts.headlines, fontSize = 30.sp)
        },
        navigationIcon = {
            if (backButton) {
                IconButton(
                    onClick = onBackButton,
                    colors = IconButtonColors(
                        containerColor = Colors.Background,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Black,
                        disabledContentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    )
}
@Composable
fun MoodMateAlertDialogue( imageVector: ImageVector=Icons.Filled.Warning, body:String="",dismissText:String="Not Now",confirmText:String="Confirm",onDismissRequest: (Boolean) -> Unit, onConfirm: () -> Unit){
    AlertDialog(onDismissRequest = { onDismissRequest(false) },
        confirmButton = {
            Button(onClick = {
                onConfirm();onDismissRequest(false)
            }, colors = getColors()) { Text(confirmText) }
        },
        dismissButton = {
            Button(
                onClick = { onDismissRequest(false) },
                colors = ButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.DarkGray,
                    Color.DarkGray,
                    Color.Black
                )
            )
            {
                    Text(dismissText)
            }
        },
        title = {
            Row(horizontalArrangement = Arrangement.Center) {
                Text("")
                Icon(
                    imageVector = imageVector,
                    contentDescription = ""
                )
            }
        },
        text = { Text(body) }
    )
}
