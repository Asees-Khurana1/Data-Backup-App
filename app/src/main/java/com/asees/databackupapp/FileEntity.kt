package com.asees.databackupapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@Entity(tableName = "files")
@IgnoreExtraProperties
data class FileEntity(
    @PrimaryKey val id: String,
    var fileName: String,
    var filePath: String,
    var lastAccessed: Long = System.currentTimeMillis(),
    var isBackedUp: Boolean = false,
    var frequentlyUsed: Boolean = false
)
