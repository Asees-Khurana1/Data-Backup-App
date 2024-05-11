package com.asees.databackupapp

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

object FirebaseDBHelper {
    private val storageRef = FirebaseStorage.getInstance().reference.child("file_data")
    private val dbRef = FirebaseDatabase.getInstance().reference.child("files")

    fun backupFile(file: FileEntity, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val fileRef = storageRef.child(file.fileName)
        val fileUri = Uri.fromFile(File(file.filePath))

        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                file.isBackedUp = true
                dbRef.child(file.id).setValue(file)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onError(exception)
                    }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    fun prefetchFile(file: FileEntity, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val localFile = File(file.filePath)
        storageRef.child(file.fileName).getFile(localFile)
            .addOnSuccessListener {
                file.isBackedUp = false
                dbRef.child(file.id).setValue(file)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onError(exception)
                    }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
}
