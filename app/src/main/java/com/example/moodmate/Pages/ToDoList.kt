package com.example.moodmate.Pages
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.moodmate.Account
import com.example.moodmate.MoodMateAlertDialogue
import com.example.moodmate.MoodMateButton
import com.example.moodmate.MoodMateTopAppBar
import com.example.moodmate.ViewModels.InternalDataBaseViewModel
import com.example.moodmate.ViewModels.TodoItem
import com.example.moodmate.ui.theme.Colors
import com.example.moodmate.ui.theme.MoodMateTheme

@Composable
fun TodoListScreen(navController: NavHostController, chartViewModel: InternalDataBaseViewModel) {
    val scrollState= rememberScrollState()
    var modeSelected by rememberSaveable { mutableStateOf("load") }
    var todoItems by rememberSaveable { mutableStateOf(listOf<TodoItem>()) }
    var newItemText by rememberSaveable { mutableStateOf("") }
    var enableDeleteButton by rememberSaveable { mutableStateOf(false) }
    var enableDialogue by rememberSaveable { mutableStateOf(false) }
    val itemToBeDeleted= remember {mutableStateOf<TodoItem?>(null)}
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    if (isRefreshing) {
        LaunchedEffect(Unit) {
            when(modeSelected) {
                "add"->{
                    chartViewModel.addTodoItem(TodoItem(id = 0, text = newItemText));todoItems=chartViewModel.getAllTodoItems();
                        newItemText = ""}
                "deleteAll"->{chartViewModel.deleteAllTodoItems();todoItems=chartViewModel.getAllTodoItems()}
                "delete"->{chartViewModel.deleteTodoItem(todoItem = itemToBeDeleted.value!!);
                          todoItems=chartViewModel.getAllTodoItems()}
                else->{todoItems=chartViewModel.getAllTodoItems()}
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
    enableDeleteButton = when(todoItems.size){
        0->{
            false
        }
        else-> {
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
                    MoodMateTopAppBar("To Do List", backButton =true){
                        navController.navigate(Account.route){
                            popUpTo(0){
                                inclusive=true
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color.White)) {
                Column(
                    modifier = Modifier.fillMaxSize().background(
                        Colors.Background
                    ).padding(start = 8.dp, end = 8.dp)
                        .verticalScroll(scrollState), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        Alignment.End
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        MoodMateTextField(
                            text = newItemText,
                            onValueChange = { newItemText = it },
                            icon = Icons.Filled.Create,
                            placeholder = "Enter Task"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        MoodMateFloatingActionButton {
                            if (newItemText.isNotBlank()) {
                                modeSelected="add"
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        todoItems.forEach { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = item.text,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Black,
                                        maxLines = 2,
                                        modifier = Modifier.weight(0.5F)
                                    )
                                    IconButton(
                                        onClick = {
                                            itemToBeDeleted.value=item
                                            modeSelected="delete"
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete task",
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
@Composable
fun MoodMateFloatingActionButton(onClick :()->Unit){
    FloatingActionButton(
        onClick = {onClick()}
        ,
        containerColor = Colors.Secondary,
        contentColor = Color.Black,
    ) {
        Icon(Icons.Default.Add, "Add task")
    }
}
