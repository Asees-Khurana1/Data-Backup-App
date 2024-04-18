package com.asees.databackupapp

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.FirebaseDatabase

object FirebaseDBHelper {

    private val storageRef = FirebaseStorage.getInstance().getReference("file_data")
    private val dbRef = FirebaseDatabase.getInstance().getReference("files")

    fun backupFile(file: FileEntity) {
        val fileRef = storageRef.child(file.fileName)
        val fileUri = Uri.fromFile(java.io.File(file.filePath))
        fileRef.putFile(fileUri).addOnSuccessListener {
            file.isBackedUp = true
            dbRef.child(file.id).setValue(file)
        }.addOnFailureListener {
            // Handle any errors in backup
        }
    }

    fun prefetchFile(file: FileEntity) {
        val fileRef = storageRef.child(file.fileName)
        fileRef.getFile(java.io.File(file.filePath)).addOnSuccessListener {
            file.isBackedUp = false
            dbRef.child(file.id).setValue(file)
        }.addOnFailureListener {
            // Handle any errors in restoration
        }
    }
}
