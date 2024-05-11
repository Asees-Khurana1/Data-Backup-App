package com.asees.databackupapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.google.firebase.annotations.concurrent.Background
import java.util.concurrent.TimeUnit
class MainActivity : ComponentActivity() {


    private lateinit var viewModel: FileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("to check 1st step","1")

        super.onCreate(savedInstanceState)
        Log.d("to check 1st step","2")

//        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
//            .get(FileViewModel::class.java)

        scheduleBackupWorker()  // Schedule backup worker on app start
        scheduleRestoreWorker() // Schedule restore worker on app start
        Log.d("to check 1st step","3")

        setContent {
            MainScreen()
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun MainScreen() {
        val backupButtonColor = ButtonDefaults.buttonColors(
            backgroundColor = Color.Green, // Set the background color of the button
            contentColor = Color.White // Set the content color of the button (text color)
        )
        val RestoreButtonColor = ButtonDefaults.buttonColors(
            backgroundColor = Color.LightGray, // Set the background color of the button
            contentColor = Color.White // Set the content color of the button (text color)
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Data Backup App") },
                    backgroundColor = Color.Gray // Set the background color of the TopAppBar
                )
//                TopAppBar(title = { Text("Data Backup App") })
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Add background image
                Image(
                    painter = painterResource(id = R.drawable.cloud), // Replace "your_image" with the actual image resource name
                    contentDescription = null, // Provide content description if necessary
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds // Scale the image to fill the bounds of the Box
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    BackupActivity::class.java
                                )
                            )
                        },
                        colors = backupButtonColor,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text("Backup Files")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    RestoreActivity::class.java
                                )
                            )
                        },
                        colors = RestoreButtonColor,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text("Restore Files")
                    }
                }
            }
        }
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
