package net.syntessense.app.todolist_dai2

import android.content.Context
import androidx.room.*

@ProvidedTypeConverter
class Converter {

    @TypeConverter
    fun convertPriorityToString(p: Todo.Priority): String {
        return p.color
    }

    @TypeConverter
    fun convertStringToPriority(color: String): Todo.Priority {
        return when(color) {
            Todo.Priority.RED.color -> Todo.Priority.RED
            Todo.Priority.ORANGE.color -> Todo.Priority.ORANGE
            Todo.Priority.GREEN.color -> Todo.Priority.GREEN
            else -> throw Exception("Wrong color for priority !")
        }
    }
}


@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}


@Entity
@TypeConverters(Converter::class)
data class Todo(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "priority") val priority: Priority,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "creationDate") val creationDate: String,
    @ColumnInfo(name = "limitDate") val limitDate: String,
    @ColumnInfo(name = "doneDate") val doneDate: String,
) {
    sealed class Priority(val color: String) {
        object RED : Priority("#ff0000")
        object ORANGE: Priority("#ff9900")
        object GREEN: Priority("#00aa00")
    }
}



@Dao
@TypeConverters(Converter::class)
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun getAll(): List<Todo>

    @Query("DELETE FROM todo WHERE 1")
    fun deleteAll(): Unit

    @Query("SELECT * FROM todo LIMIT :offset, :length")
    fun getPage(offset: Int, length: Int): List<Todo>

    @Update
    fun update(todo: Todo)


    @Insert
    fun insertAll(vararg todo: Todo)

    @Delete
    fun delete(user: Todo)
}

fun getTodoDb(applicationContext: Context): TodoDatabase {
    return Room.databaseBuilder(
        applicationContext,
        TodoDatabase::class.java, "database-name"
    ).addTypeConverter(Converter()).build()
}

