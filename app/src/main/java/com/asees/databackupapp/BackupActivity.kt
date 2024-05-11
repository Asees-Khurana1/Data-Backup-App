package com.asees.databackupapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

class BackupActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackupScreen()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun BackupScreen() {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Backup Files") })
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
                        if (DeviceStatusUtils.hasEnoughBattery(applicationContext) && DeviceStatusUtils.isNetworkAvailable(applicationContext)) {
                            backupFile(FileEntity(id = "1", fileName = "test.txt", filePath = "/path/to/test.txt"))
                        } else {
                            println("Not enough battery or network is not available")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Start Backup")
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun backupFile(file: FileEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                FirebaseDBHelper.backupFile(
                    file,
                    onSuccess = {
                        println("Backup successful for file: ${file.fileName}")
                        // Ensure you update the database in a coroutine scope
                        launch {
                            try {
                                val repository = FileRepository(applicationContext)
                                repository.updateFile(file.apply { isBackedUp = true })
                            } catch (e: Exception) {
                                println("Error updating file status: ${e.localizedMessage}")
                            }
                        }
                    },
                    onError = { exception ->
                        println("Backup failed for file: ${file.fileName}, Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                println("Backup process failed: ${e.localizedMessage}")
            }
        }
    }
}
