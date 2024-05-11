package com.asees.databackupapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FileDao {
    @Query("SELECT * FROM files WHERE lastAccessed <= :threshold AND isBackedUp = 0")
    fun getFilesForBackup(threshold: Long): List<FileEntity>

    @Query("SELECT * FROM files WHERE frequentlyUsed = 1 AND isBackedUp = 1")
    fun getFilesToRestore(): List<FileEntity>

    @Insert
    fun insertFile(file: FileEntity)

    @Update
    fun updateFile(file: FileEntity)
}
