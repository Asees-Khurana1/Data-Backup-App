package com.asees.databackupapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

class BackupActivity : ComponentActivity() {
    private val repository by lazy { FileRepository(applicationContext) }

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
        var backupProgress by remember { mutableDoubleStateOf(0.0) }
        var isBackingUp by remember { mutableStateOf(false) } // This is a MutableState<Boolean>

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
                            val file = FileEntity(id = "1", fileName = "test.txt", filePath = "/path/to/test.txt")
                            isBackingUp = true
                            // Pass isBackingUp as a MutableState using 'remember { mutableStateOf }'
                            backupFile(file, onProgressUpdate = { progress ->
                                backupProgress = progress
                            }, onCompletion = { success, error ->
                                if (success) {
                                    Toast.makeText(applicationContext, "Backup completed successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(applicationContext, "Backup failed: $error", Toast.LENGTH_SHORT).show()
                                }
                                isBackingUp = false
                            })
                        } else {
                            Toast.makeText(applicationContext, "Not enough battery or network is not available", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    enabled = !isBackingUp
                ) {
                    Text("Start Backup")
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (isBackingUp) {
                    LinearProgressIndicator(
                        progress = backupProgress.toFloat() / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${backupProgress.toInt()}%")
                }
            }
        }
    }

    private fun backupFile(file: FileEntity, onProgressUpdate: (Double) -> Unit, onCompletion: (Boolean, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                FirebaseDBHelper.backupFile(
                    file,
                    onProgress = { progress ->
                        launch(Dispatchers.Main) {
                            onProgressUpdate(progress)
                        }
                    },
                    onSuccess = {
                        launch(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Backup completed successfully!", Toast.LENGTH_SHORT).show()
                        }
                        launch(Dispatchers.IO) {
                            try {
                                repository.updateFile(file.apply { isBackedUp = true })
                            } catch (e: Exception) {
                                launch(Dispatchers.Main) {
                                    Toast.makeText(applicationContext, "Error updating file status: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    onError = { exception ->
                        launch(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Backup failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                        println("Backup failed for file: ${file.fileName}, Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Backup process failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
