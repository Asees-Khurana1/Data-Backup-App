package com.asees.databackupapp

import androidx.room.*

@Dao
interface FileDao {
    @Query("SELECT * FROM files WHERE lastAccessed <= :threshold AND isBackedUp = 0")
    suspend fun getFilesForBackup(threshold: Long): List<FileEntity>

    @Query("SELECT * FROM files WHERE frequentlyUsed = 1 AND isBackedUp = 1")
    suspend fun getFilesToRestore(): List<FileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity)

    @Update
    suspend fun updateFile(file: FileEntity)

    @Query("UPDATE files SET lastAccessed = :lastAccessed WHERE id = :fileId")
    suspend fun updateFileAccess(fileId: String, lastAccessed: Long)
}
