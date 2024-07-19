package com.asees.databackupapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
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

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import androidx.core.app.ActivityCompat

class BackupActivity : ComponentActivity() {
    private val repository by lazy { FileRepository(applicationContext) }

    private var backupProgress = mutableDoubleStateOf(0.0)
    private var isBackingUp = mutableStateOf(false)

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1000
        private const val THIRTY_DAYS = 60 * 24 * 60 * 60 * 1000L // 60 days in milliseconds
    }

    @RequiresApi(VERSION_CODES.R)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackupScreen()
        }
        if (SDK_INT >= VERSION_CODES.M) {
            if (!hasStoragePermission()) {
                requestStoragePermissions()
            }
        }
    }

    @RequiresApi(VERSION_CODES.R)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun BackupScreen() {
        Scaffold(topBar = {
            TopAppBar(title = { Text("Backup Files") })
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (hasStoragePermission()) {
                            isBackingUp.value = true
                            startBackup()
                        } else {
                            requestStoragePermissions()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    enabled = !isBackingUp.value
                ) {
                    Text("Start Backup")
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (isBackingUp.value) {
                    LinearProgressIndicator(
                        progress = backupProgress.doubleValue.toFloat() / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${(backupProgress.doubleValue).toInt()}%")
                }
            }
        }
    }

    private fun hasStoragePermission(): Boolean {
        return if (SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE) {
            true
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(VERSION_CODES.R)
    private fun requestStoragePermissions() {
        if (SDK_INT >= VERSION_CODES.M && SDK_INT < VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        } else {
            val intent = Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
                startBackup()
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFilesNotAccessedInLastMonth(): List<FileEntity> {
        val oneMonthAgo = System.currentTimeMillis() - THIRTY_DAYS
        val filesList = mutableListOf<FileEntity>()
        val downloadsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)

        fun checkFiles(directory: File) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (!file.isDirectory && file.lastModified() < oneMonthAgo) {
                        filesList.add(FileEntity(id = file.name, fileName = file.name, filePath = file.path))
                    }
                }
            }
        }

        checkFiles(downloadsDir)
        return filesList
    }

    private fun startBackup() {
        CoroutineScope(Dispatchers.Main).launch {
            isBackingUp.value = true
            val filesToBackup = getFilesNotAccessedInLastMonth()
            if (filesToBackup.isEmpty()) {
                Toast.makeText(this@BackupActivity, "No files to back up", Toast.LENGTH_SHORT).show()
                isBackingUp.value = false
                return@launch
            }

            filesToBackup.forEach { file ->
                backupFile(file, onProgressUpdate = { progress ->
                    backupProgress.doubleValue = progress
                }, onCompletion = { success, error ->
                    if (success) {
                        Toast.makeText(this@BackupActivity, "Backup of ${file.fileName} completed successfully!", Toast.LENGTH_SHORT).show()
                        // After successful backup, delete the file from the device
                        deleteFileFromDevice(file.filePath)
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
                val sanitizedFileName = sanitizeFileName(file.fileName)
                FirebaseDBHelper.backupFile(
                    file.copy(fileName = sanitizedFileName),
                    onProgress = { progress ->
                        CoroutineScope(Dispatchers.Main).launch {
                            onProgressUpdate(progress)
                        }
                    },
                    onSuccess = {
                        try {
                            launch(Dispatchers.IO){
                                repository.updateFile(file.apply { isBackedUp = true })
                                CoroutineScope(Dispatchers.Main).launch {
                                    onCompletion(true, null)
                                    // Notify the user that the file was backed up successfully
                                    Toast.makeText(applicationContext, "File backed up and deleted from device: ${file.fileName}", Toast.LENGTH_SHORT).show()
                                }
                                // After successful backup, delete the file from the device
                                deleteFileFromDevice(file.filePath)
                            }
                        } catch (e: Exception) {
                            CoroutineScope(Dispatchers.Main).launch {
                                onCompletion(false, "Error updating file status: ${e.localizedMessage}")
                            }
                        }
                    },
                    onError = { exception ->
                        CoroutineScope(Dispatchers.Main).launch {
                            onCompletion(false, "Backup failed: ${exception.message}")
                        }
                    }
                )
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    onCompletion(false, "Backup process failed: ${e.localizedMessage}")
                }
            }
        }
    }


    private fun deleteFileFromDevice(filePath: String) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                if (file.delete()) {
                    runOnUiThread {
                        Toast.makeText(this, "File deleted from device: ${file.name}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Failed to delete file from device: ${file.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this, "Error deleting file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sanitizeFileName(fileName: String): String {
        return fileName.replace(".", "_")
            .replace("#", "_")
            .replace("$", "_")
            .replace("[", "_")
            .replace("]", "_")
    }
}
