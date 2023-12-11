package com.example.jampez.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.jampez.utils.constants.COMPLETED
import com.example.jampez.utils.constants.USERID

@Entity
class ToDo(
    @field:PrimaryKey var id: Long,
    @field:ColumnInfo(name = com.example.jampez.utils.constants.TODO) var todo: String,
    @field:ColumnInfo(name = COMPLETED) var completed: Boolean,
    @field:ColumnInfo(name = USERID) var userId: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ToDo

        if (id != other.id) return false
        if (todo != other.todo) return false
        if (completed != other.completed) return false
        return userId == other.userId
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + todo.hashCode()
        result = 31 * result + completed.hashCode()
        result = 31 * result + userId.hashCode()
        return result
    }

}