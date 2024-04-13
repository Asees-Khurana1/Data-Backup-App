package com.asees.databackupapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MainScreen() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Data Backup App",
                            color = Color.White
                        )
                    },
                    backgroundColor = MaterialTheme.colors.primary
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.LightGray), // Changed background color
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        startActivity(Intent(this@MainActivity, BackupActivity::class.java))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 32.dp), // Adjusted padding
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    elevation = ButtonDefaults.elevation(12.dp), // Added elevation
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        text = "Go to Backup",
                        color = Color.White
                    )
                }
                Button(
                    onClick = {
                        startActivity(Intent(this@MainActivity, RestoreActivity::class.java))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 32.dp), // Adjusted padding
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                    elevation = ButtonDefaults.elevation(12.dp), // Added elevation
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        text = "Go to Restore",
                        color = Color.White
                    )
                }
            }
        }
    }
}
