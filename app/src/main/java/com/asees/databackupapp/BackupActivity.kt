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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

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
                            val backupRequest = OneTimeWorkRequestBuilder<BackupWorker>().build()
                            WorkManager.getInstance(applicationContext).enqueue(backupRequest)
                            Toast.makeText(applicationContext, "Backup started", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "Insufficient battery or network not available", Toast.LENGTH_LONG).show()
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
}
