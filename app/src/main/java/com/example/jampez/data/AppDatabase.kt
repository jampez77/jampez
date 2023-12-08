package com.example.jampez.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jampez.data.dao.ToDoDao
import com.example.jampez.data.entities.ToDo

@Database(
    entities = [
        ToDo::class,
    ],
    version = 1,
    exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): ToDoDao

    fun clearDatabase() {
        clearAllTables()
    }
}