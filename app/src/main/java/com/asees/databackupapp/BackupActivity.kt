package com.asees.databackupapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class BackupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackupScreen()
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun BackupScreen() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Backup Files") },
                    backgroundColor = MaterialTheme.colors.primary
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Press the button to start the backup process.",
                    style = MaterialTheme.typography.body1,
                    color = Color.Black
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Implementation for the actual backup logic
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp), // Adjusted button height
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        text = "Start Backup",
                        style = MaterialTheme.typography.button,
                        color = Color.White
                    )
                }
            }
        }
    }
}