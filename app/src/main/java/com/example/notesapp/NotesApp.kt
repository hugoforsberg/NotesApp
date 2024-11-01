package com.example.notesapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class NoteItem(
    val noteID: Int,
    var title: String,
    var description: String,
    val check: MutableState<Boolean> = mutableStateOf(false)
)

@Composable
fun NotesApp() {
    val navController = rememberNavController()
    val noteList = remember { mutableStateListOf<NoteItem>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        NavHost(navController = navController, startDestination = "noteList", Modifier.padding(padding)) {
            composable("noteList") {
                NoteListScreen(navController, noteList, snackbarHostState, coroutineScope)
            }
            composable("addNote") {
                AddNoteScreen(navController, noteList, snackbarHostState, coroutineScope)
            }
            composable("editNote/{id}") { backStackEntry ->
                val itemID = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                val noteItem = noteList.find { it.noteID == itemID }
                noteItem?.let { EditNoteScreen(navController, it, snackbarHostState, coroutineScope) }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    navController: NavController,
    noteList: MutableList<NoteItem>,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Note List") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addNote") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(noteList) { item ->
                ListItem(
                    leadingContent = {
                        Checkbox(
                            checked = item.check.value,
                            onCheckedChange = {
                                item.check.value = !item.check.value
                            })},
                    headlineContent = { Text(item.title) },
                    supportingContent = { Text(item.description)},
                    trailingContent = {
                        Row {
                            IconButton(
                                onClick = { navController.navigate("editNote/${item.noteID}") }
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Note")
                            }
                            IconButton(
                                onClick = {
                                    val removedNote = item
                                    noteList.remove(item)
                                    coroutineScope.launch {
                                        val snackbarResult = snackbarHostState.showSnackbar(
                                            message = "Note deleted",
                                            actionLabel = "Undo"
                                        )
                                        if(snackbarResult == SnackbarResult.ActionPerformed) {
                                            noteList.add(removedNote)
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Note")
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    navController: NavController,
    noteList: MutableList<NoteItem>,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>("") }
    var descriptionError by remember { mutableStateOf<String?>("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Note") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()

                        }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = title,
                onValueChange = {title = it},
                label = { Text("Title") },
                isError = titleError != ""
            )
            if (titleError != "") {
                Text(text = titleError!!, color = androidx.compose.ui.graphics.Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = description,
                onValueChange = {description = it},
                label = { Text("Description") },
                isError = descriptionError != ""
            )
            if (descriptionError != "") {
                Text(text = descriptionError!!, color = androidx.compose.ui.graphics.Color.Red)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                titleError = when{
                    title.length > 50 -> "Title cannot exceed 50 characters"
                    title.length < 3 -> "Title must be at least 3 characters"
                    else -> ""
                }
                descriptionError = if (description.length > 120) {
                    "Description cannot exceed 120 characters"
                } else ""

                if (titleError == "" && descriptionError == ""
                    && title.isNotBlank() && description.isNotBlank()) {
                    noteList.add(
                        NoteItem(
                            noteID = noteList.size,
                            title = title,
                            description = description
                        )
                    )
                    navController.popBackStack()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Note added")
                    }
                }

            }) {
                Text("Add Note")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    navController: NavController,
    noteItem: NoteItem,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope) {
    var title by remember { mutableStateOf(noteItem.title) }
    var description by remember { mutableStateOf(noteItem.description) }
    var titleError by remember { mutableStateOf<String?>("") }
    var descriptionError by remember { mutableStateOf<String?>("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Note") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = title,
                onValueChange = { title = it},
                label = { Text("Title") },
                isError = titleError != ""
            )
            if (titleError != "") {
                Text(text = titleError!!, color = androidx.compose.ui.graphics.Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = description,
                onValueChange = {description = it},
                label = { Text("Description") },
                isError = descriptionError != "",
            )
            if (descriptionError != ""){
                Text(text = descriptionError!!, color = androidx.compose.ui.graphics.Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                titleError = when{
                    title.length > 50 -> "Title cannot exceed 50 characters"
                    title.length < 3 -> "Title must be at least 3 characters"
                    else -> ""
                }
                descriptionError = if (description.length > 120) {
                    "Description cannot exceed 120 characters"
                } else ""

                if (titleError == "" && descriptionError == ""
                    && title.isNotBlank() && description.isNotBlank()) {
                    noteItem.title = title
                    noteItem.description = description
                    navController.popBackStack()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Note edited")
                    }
                }
            }){
                Text("Save Note")
            }
        }
    }

}


