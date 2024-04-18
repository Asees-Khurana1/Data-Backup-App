package com.asees.databackupapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@Entity(tableName = "files")
@IgnoreExtraProperties  // Firebase annotation to ignore fields not listed
data class FileEntity(
    @PrimaryKey val id: String = "",  // Use a default value for compatibility with Firebase
    var fileName: String = "",
    var filePath: String = "",
    var lastAccessed: Long = 0,
    var isBackedUp: Boolean = false,
    var frequentlyUsed: Boolean = false  // Example additional field
)
