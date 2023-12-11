package com.example.jampez.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import androidx.room.Update
import com.example.jampez.data.entities.ToDo

@Dao
interface ToDoDao {

    @Insert(onConflict = IGNORE)
    fun insertAll(todos: List<ToDo>)

    @Query("SELECT * FROM todo")
    fun getAll(): List<ToDo>

    @Query("SELECT * FROM todo")
    fun getAllLiveData(): LiveData<List<ToDo>>

    @Query("DELETE FROM todo WHERE userId LIKE :userId")
    fun emptyTable(userId: Long)

    @Update
    fun update(toDo: ToDo)
}