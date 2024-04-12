package com.asees.databackupapp

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseDBHelper {
    private var database: DatabaseReference = FirebaseDatabase.getInstance().getReference("files")

    fun backupFile(file: FileEntity) {
        file.id = database.push().key // Generate unique key for each file
        file.isBackedUp = true
        file.id?.let {
            database.child(it).setValue(file)
        }
    }

    fun getFile(fileId: String, callback: (FileEntity?) -> Unit) {
        database.child(fileId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val file = snapshot.getValue(FileEntity::class.java)
                callback(file)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value: ${error.toException()}")
                callback(null)
            }
        })
    }

    // Implement more functions as needed
}
