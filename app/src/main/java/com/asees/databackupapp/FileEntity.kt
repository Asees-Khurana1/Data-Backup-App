package com.asees.databackupapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FileEntity(
    @PrimaryKey var id: String,
    val fileName: String,
    val filePath: String,
    var lastAccessed: Long,
    var isBackedUp: Boolean,
    var frequentlyUsed: Boolean = false
)
