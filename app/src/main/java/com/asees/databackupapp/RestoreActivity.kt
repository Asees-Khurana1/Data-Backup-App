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

class RestoreActivity : ComponentActivity() {
    private val repository by lazy { FileRepository(applicationContext) }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RestoreScreen()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun RestoreScreen() {
        var restoreProgress by remember { mutableDoubleStateOf(0.0) }
        var isRestoring by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Restore Files") })
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
                            isRestoring = true
                            restoreFile(file, onProgressUpdate = { progress ->
                                restoreProgress = progress
                            }, onCompletion = { success, error ->
                                isRestoring = false
                                if (success) {
                                    Toast.makeText(applicationContext, "Restore completed successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(applicationContext, "Restore failed: $error", Toast.LENGTH_SHORT).show()
                                }
                            })
                        } else {
                            Toast.makeText(applicationContext, "Not enough battery or network is not available", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    enabled = !isRestoring
                ) {
                    Text("Restore Files")
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (isRestoring) {
                    LinearProgressIndicator(
                        progress = restoreProgress.toFloat() / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${restoreProgress.toInt()}%")
                }
            }
        }
    }

    private fun restoreFile(file: FileEntity, onProgressUpdate: (Double) -> Unit, onCompletion: (Boolean, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                FirebaseDBHelper.prefetchFile(
                    file,
                    onProgress = { progress ->
                        launch(Dispatchers.Main) {
                            onProgressUpdate(progress)
                        }
                    },
                    onSuccess = {
                        launch(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Restore completed successfully!", Toast.LENGTH_SHORT).show()
                        }
                        launch(Dispatchers.IO) {
                            try {
                                repository.updateFile(file.apply { isBackedUp = false })
                                launch(Dispatchers.Main) {
                                    onCompletion(true, null)
                                }
                            } catch (e: Exception) {
                                launch(Dispatchers.Main) {
                                    onCompletion(false, "Error updating file status: ${e.localizedMessage}")
                                    Toast.makeText(applicationContext, "Error updating file status: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    onError = { exception ->
                        launch(Dispatchers.Main) {
                            onCompletion(false, "Restore failed: ${exception.message}")
                            Toast.makeText(applicationContext, "Restore failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                        println("Restore failed for file: ${file.fileName}, Error: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    onCompletion(false, "Restore process failed: ${e.localizedMessage}")
                    Toast.makeText(applicationContext, "Restore process failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
