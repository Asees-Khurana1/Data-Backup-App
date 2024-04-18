package com.asees.databackupapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FileDao {
    @Query("SELECT * FROM files WHERE lastAccessed <= :threshold AND isBackedUp = 0")
    fun getFilesForBackup(threshold: Long): List<FileEntity>

    @Query("SELECT * FROM files WHERE frequentlyUsed = 1 AND isBackedUp = 1")
    fun getFilesForRestore(): List<FileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(file: FileEntity)

    @Update
    fun updateFile(file: FileEntity)

    @Delete
    fun deleteFile(file: FileEntity)
}

