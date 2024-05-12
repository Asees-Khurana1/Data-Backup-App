package com.asees.databackupapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import java.io.File

class BackupActivity : ComponentActivity() {
    private val repository by lazy { FileRepository(applicationContext) }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackupScreen()
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermissions()
        } else {
            startBackup()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun BackupScreen() {
        var backupProgress by remember { mutableStateOf(0.0) }
        var isBackingUp by remember { mutableStateOf(false) }

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
                            startBackup()
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

    private fun requestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
                    startBackup()
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFilesNotAccessedInLastMonth(): List<FileEntity> {
        val monthAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        val filesList = mutableListOf<FileEntity>()
        val externalStorage = Environment.getExternalStorageDirectory()

        fun checkFiles(directory: File) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        checkFiles(file)
                    } else {
                        if (file.lastModified() < monthAgo) {
                            filesList.add(FileEntity(id = file.name, fileName = file.name, filePath = file.path))
                        }
                    }
                }
            }
        }

        checkFiles(externalStorage)
        return filesList
    }

    private fun startBackup() {
        CoroutineScope(Dispatchers.Main).launch {
            val filesToBackup = getFilesNotAccessedInLastMonth()
            if (filesToBackup.isEmpty()) {
                Toast.makeText(this@BackupActivity, "No files to back up", Toast.LENGTH_SHORT).show()
                return@launch
            }

            filesToBackup.forEach { file ->
                backupFile(file, onProgressUpdate = { progress ->
                    // Log or update progress UI
                }, onCompletion = { success, error ->
                    if (success) {
                        Toast.makeText(this@BackupActivity, "Backup of ${file.fileName} completed successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@BackupActivity, "Backup of ${file.fileName} failed: $error", Toast.LENGTH_SHORT).show()
                    }
                })
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
                        try {
                            launch(Dispatchers.IO){
                                repository.updateFile(file.apply { isBackedUp = true })
                                launch(Dispatchers.Main) {
                                    onCompletion(true, null)
                                }
                            }
                        } catch (e: Exception) {
                            launch(Dispatchers.Main) {
                                onCompletion(false, "Error updating file status: ${e.localizedMessage}")
                            }
                        }
                    },
                    onError = { exception ->
                        launch(Dispatchers.Main) {
                            onCompletion(false, "Backup failed: ${exception.message}")
                        }
                    }
                )
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    onCompletion(false, "Backup process failed: ${e.localizedMessage}")
                }
            }
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1000
    }
}
