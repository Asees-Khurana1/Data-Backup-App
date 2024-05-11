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
import androidx.compose.ui.unit.dp
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
        scheduleBackupWorker()
        scheduleRestoreWorker()
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MainScreen() {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Data Backup App") })
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
                    onClick = { navigateToBackupActivity() },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Backup Files")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navigateToRestoreActivity() },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Restore Files")
                }
            }
        }
    }

    private fun navigateToBackupActivity() {
        startActivity(Intent(this, BackupActivity::class.java))
    }

    private fun navigateToRestoreActivity() {
        startActivity(Intent(this, RestoreActivity::class.java))
    }

    private fun scheduleBackupWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val backupRequest = PeriodicWorkRequestBuilder<BackupWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "file_backup_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            backupRequest
        )
    }

    private fun scheduleRestoreWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val restoreRequest = PeriodicWorkRequestBuilder<RestoreWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "file_restore_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            restoreRequest
        )
    }
}
