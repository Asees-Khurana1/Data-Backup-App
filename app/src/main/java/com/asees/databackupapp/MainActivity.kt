package com.asees.databackupapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                    title = { Text("Data Backup App", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    backgroundColor = Color.Blue,
                    contentColor = Color.White
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        startActivity(Intent(this@MainActivity, BackupActivity::class.java))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
                ) {
                    Text("Go to Backup", color = Color.White, fontSize = 16.sp)
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        startActivity(Intent(this@MainActivity, RestoreActivity::class.java))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
                ) {
                    Text("Go to Restore", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
