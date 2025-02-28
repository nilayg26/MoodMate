package com.example.moodmate.Pages
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.moodmate.Account
import com.example.moodmate.MoodMateAlertDialogue
import com.example.moodmate.MoodMateButton
import com.example.moodmate.MoodMateTopAppBar
import com.example.moodmate.ViewModels.EmergencyContact
import com.example.moodmate.ViewModels.InternalDataBaseViewModel
import com.example.moodmate.getColors
import com.example.moodmate.ui.theme.Colors
import com.example.moodmate.ui.theme.Fonts
import com.example.moodmate.ui.theme.MoodMateTheme

@Composable
fun EmergencyContacts(navController: NavHostController, chartViewModel: InternalDataBaseViewModel) {
    var contacts by remember { mutableStateOf(listOf<EmergencyContact>()) }
    var newContactName by rememberSaveable { mutableStateOf("") }
    var newPhoneNumber by rememberSaveable { mutableStateOf("") }
    val contactToBeDeleted= remember {mutableStateOf<EmergencyContact?>(null)}
    var showError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var enableDeleteButton by rememberSaveable { mutableStateOf(false) }
    var enableDialogue by rememberSaveable { mutableStateOf(false) }
    var enablePhoneDialogue by rememberSaveable { mutableStateOf(false) }
    var modeSelected by remember { mutableStateOf("load") }
    val scrollState= rememberScrollState()
    val context= LocalContext.current
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    if (isRefreshing) {
        LaunchedEffect(Unit) {
                when(modeSelected) {
                    "add"->{
              chartViewModel.addContact(EmergencyContact(id = 0,name = newContactName, phoneNumber = newPhoneNumber));contacts=chartViewModel.getAllContacts();newContactName = ""
                        newPhoneNumber = ""}
                    "deleteAll"->{chartViewModel.deleteAllContacts();contacts=chartViewModel.getAllContacts()}
                    "delete"->{chartViewModel.deleteContact(contactToBeDeleted.value!!);contacts=chartViewModel.getAllContacts()}
                    else->{contacts = chartViewModel.getAllContacts()}
                }
                modeSelected=""
                isRefreshing = false
        }
    }
    LaunchedEffect(modeSelected){
            if(modeSelected!=""){
                isRefreshing=true
            }
    }
    var phoneNumber by rememberSaveable{
        mutableStateOf("")
    }
    enableDeleteButton = when(contacts.size){
        0->{
            false
        }
        else->{
            true
        }
    }
    MoodMateTheme {
        Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MoodMateTopAppBar("Emergency Contact", backButton =true){
                        navController.navigate(Account.route){
                            popUpTo(0){
                                inclusive=true
                            }
                        }
                    }
                }
            }
            ) { padding->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(
                    modifier = Modifier.fillMaxSize().background(Colors.Background).padding(start = 8.dp, end = 8.dp).verticalScroll(scrollState), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (enablePhoneDialogue) {
                            MoodMateAlertDialogue(
                                imageVector = Icons.Filled.Phone,
                                body = "You will be directed to dialer",
                                onDismissRequest = { enablePhoneDialogue = it }) {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$phoneNumber")
                                }
                                context.startActivity(intent)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        MoodMateTextField(icon = Icons.Filled.Person, onValueChange = {newContactName=it}, placeholder = "Contact Name", text = newContactName)
                        MoodMateTextField(icon = Icons.Filled.Phone, onValueChange = {newPhoneNumber=it}, placeholder = "Phone Number", text = newPhoneNumber, numpad = true)
                        AnimatedVisibility(showError) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Button(
                            onClick = {
                                validateAndAddContact(
                                    newContactName = newContactName,
                                    newPhoneNumber = newPhoneNumber,
                                    addContact = {
                                        modeSelected="add"
                                        showError = false
                                    },
                                    onError = { it1, it2 -> errorMessage = it1; showError = it2 }
                                )
                            },
                            modifier = Modifier.align(Alignment.End),
                            colors = getColors(tertiary = false)
                        ) {
                            Icon(Icons.Default.Add, "Add contact")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Contact")
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        contacts.forEach { contact ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    phoneNumber=contact.phoneNumber
                                    enablePhoneDialogue=true
                                },
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(0.5F)) {
                                        Text(
                                            text = contact.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Black,
                                        )
                                        Text(
                                            text = contact.phoneNumber,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.DarkGray
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            contactToBeDeleted.value=contact
                                            modeSelected="delete"
                                        }
                                    ) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "Delete contact",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    MoodMateButton("Delete All", onClick = { enableDialogue=true}, enabled = enableDeleteButton)
                    if (enableDialogue){
                        MoodMateAlertDialogue(
                            imageVector = Icons.Filled.Delete,
                            body = "All your items will be permanently deleted",
                            onDismissRequest = {
                                enableDialogue=it
                            },
                            confirmText = "Delete All"
                        ) {
                            modeSelected="deleteAll"
                        }
                    }
                }
            }
        }
    }
}
fun validatePhoneNumber(number: String): Boolean {
    return number.length==10 && number.all {  it.isDigit() }
}
fun validateAndAddContact(newPhoneNumber:String, newContactName:String,addContact:()->(Unit) ,onError:(String,Boolean)->Unit) {
    when {
        newContactName.isEmpty() -> {
            onError("Please enter a name",true)
        }
        !validatePhoneNumber(newPhoneNumber) -> {
            onError("Please enter a valid phone number",true)
        }
        else -> {
            addContact()
        }
    }
}

@Composable
fun MoodMateTextField(text:String="",icon:ImageVector,onValueChange:(String)->(Unit),placeholder:String="",numpad:Boolean=false){
    TextField(
        value = text,
        onValueChange ={ onValueChange(it)},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, fontFamily = Fonts.paragraph, fontSize = 16.sp) },
        leadingIcon = {
            Icon(imageVector = icon, "null")
        },
        keyboardOptions = if (numpad)KeyboardOptions(keyboardType = KeyboardType.Phone) else KeyboardOptions(keyboardType = KeyboardType.Unspecified),
        maxLines = 2,
        textStyle = TextStyle(
            fontFamily = Fonts.paragraph,
            fontSize = 16.sp
        ),
        colors = TextFieldDefaults.colors(focusedTextColor = Color.Black)
    )
}
