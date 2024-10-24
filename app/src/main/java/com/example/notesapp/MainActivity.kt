package com.example.notesapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notesapp.ui.theme.NotesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NotesAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Row(modifier = Modifier.fillMaxWidth()){
                        Column(modifier =
                        Modifier
                            .padding(innerPadding)
                            .background(Color.Red)
                        ) {
                            Text("hello")
                            Text("world")
                            Spacer(modifier = Modifier.height(20.dp))
                            Text("button down there")
                            Spacer(modifier = Modifier.height(20.dp))

                            Button(onClick = {
                                Toast.makeText(this@MainActivity, "Button Clicked", Toast.LENGTH_SHORT).show()
                            }){
                                Text("click me")
                            }

                        }
                        Spacer(modifier = Modifier.padding(20.dp))
                        Column(modifier =
                        Modifier
                            .padding(innerPadding)
                            .background(Color.Red)
                        ) {
                            Text("hello")
                            Text("world")
                            Spacer(modifier = Modifier.height(20.dp))
                            Text("button down there")
                            Spacer(modifier = Modifier.height(20.dp))

                            Button(onClick = {

                            }){
                                Text("click me")
                            }

                        }
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Wassup, $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesAppTheme {
        Greeting("Android")
    }
}