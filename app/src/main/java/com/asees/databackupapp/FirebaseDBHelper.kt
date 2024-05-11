package com.asees.databackupapp

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

object FirebaseDBHelper {
    private val storageRef = FirebaseStorage.getInstance().getReference("file_data")
    private val dbRef = FirebaseDatabase.getInstance().getReference("files")

    fun backupFile(
        file: FileEntity,
        onProgress: (Double) -> Unit,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val fileRef = storageRef.child(file.fileName)
        val fileUri = Uri.fromFile(File(file.filePath))

        val uploadTask = fileRef.putFile(fileUri)
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            onProgress(progress)
        }.addOnSuccessListener {
            file.isBackedUp = true
            dbRef.child(file.id).setValue(file).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { exception ->
                onError(exception)
            }
        }.addOnFailureListener { exception ->
            onError(exception)
        }
    }

    fun prefetchFile(
        file: FileEntity,
        onProgress: (Double) -> Unit,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val fileRef = storageRef.child(file.fileName)
        val localFile = File(file.filePath)

        val downloadTask = fileRef.getFile(localFile)
        downloadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            onProgress(progress)
        }.addOnSuccessListener {
            file.isBackedUp = false
            dbRef.child(file.id).setValue(file).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { exception ->
                onError(exception)
            }
        }.addOnFailureListener { exception ->
            onError(exception)
        }
    }
}
