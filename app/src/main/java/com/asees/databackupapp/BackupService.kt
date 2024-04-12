package com.asees.databackupapp

import android.os.Build
import androidx.annotation.RequiresApi
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class BackupResponse(val success: Boolean, val message: String)

interface BackupService {

    @POST("backup/files")
    fun backupFile(@Body file: FileEntity): Call<Response<BackupResponse>>

    @GET("backup/files")
    fun getFilesToBackup(): List<FileEntity> // Assuming FileEntity is your data model

    companion object {
        fun create(): BackupService {
            return Retrofit.Builder()
                .baseUrl("https://your-backup-service-url.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BackupService::class.java)
        }
    }
}

class BackupManager(private val backupService: BackupService, private val fileRepository: FileRepository) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun backupFiles() {
        val filesToBackup = fileRepository.getFilesToBackup()
        filesToBackup.forEach { file ->
            backupService.backupFile(file)
            // After successful backup, mark the file as backed up in the database
        }
    }
}