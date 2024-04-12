package com.asees.databackupapp

import androidx.room.Entity
import com.google.firebase.database.IgnoreExtraProperties

@Entity(tableName = "files")
@IgnoreExtraProperties
data class FileEntity(
    var id: String? = null,
    var fileName: String? = null,
    var filePath: String? = null,
    var lastAccessed: Long? = null,
    var isBackedUp: Boolean? = null
)

