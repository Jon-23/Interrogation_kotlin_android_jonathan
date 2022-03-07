package net.syntessense.app.todolist_dai2

import androidx.room.*

@Database(entities = [Todo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): TodoDao
}


@Entity
data class Todo(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "priority") val priority: Priority,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "creationDate") val creationDate: String,
    @ColumnInfo(name = "limitDate") val limitDate: String,
    @ColumnInfo(name = "doneDate") val doneDate: String,
) {
    enum class Priority {RED, ORANGE, GREEN}
}


@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun getAll(): List<Todo>

    @Insert
    fun insertAll(vararg todo: Todo)

    @Delete
    fun delete(user: Todo)
}