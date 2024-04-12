package com.asees.databackupapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FileDao {

    @Query("SELECT * FROM FileEntity WHERE lastAccessed <= :threshold AND is_backed_up = 0")
    fun getFilesNotAccessedInLastMonth(threshold: Long): List<FileEntity>

    @Update
    fun update(file: FileEntity)

    // You might also need methods to insert and delete records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(file: FileEntity)

    @Delete
    fun delete(file: FileEntity)
}
