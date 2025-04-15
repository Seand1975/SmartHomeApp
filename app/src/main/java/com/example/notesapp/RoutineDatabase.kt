package com.example.notesapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.*

data class RoutineTask(
    val id: Int,
    val name: String,
    val time: LocalTime,
    val recurrence: String
)

class RoutineDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "routines.db"
        private const val DATABASE_VERSION = 1

        // Table and column names
        private const val TABLE_ROUTINES = "routines"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_RECURRENCE = "recurrence"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_ROUTINES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_TIME TEXT NOT NULL,
                $COLUMN_RECURRENCE TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROUTINES")
        onCreate(db)
    }

    // Add a new task
    @RequiresApi(Build.VERSION_CODES.O)
    fun addTask(task: RoutineTask): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, task.name)
            put(COLUMN_TIME, task.time.format(ISO_LOCAL_TIME))
            put(COLUMN_RECURRENCE, task.recurrence)
        }

        val id = db.insert(TABLE_ROUTINES, null, values)
        db.close()
        return id
    }

    // Get all tasks
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllTasks(): List<RoutineTask> {
        val taskList = mutableListOf<RoutineTask>()
        val query = "SELECT * FROM $TABLE_ROUTINES"
        val db = this.readableDatabase

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val timeString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
                val recurrence = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECURRENCE))

                val time = LocalTime.parse(timeString, ISO_LOCAL_TIME)

                val task = RoutineTask(id, name, time, recurrence)
                taskList.add(task)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return taskList
    }

    // Update an existing task
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTask(task: RoutineTask): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, task.name)
            put(COLUMN_TIME, task.time.format(ISO_LOCAL_TIME))
            put(COLUMN_RECURRENCE, task.recurrence)
        }

        val result = db.update(
            TABLE_ROUTINES,
            values,
            "$COLUMN_ID = ?",
            arrayOf(task.id.toString())
        )

        db.close()
        return result
    }

    // Delete a task
    fun deleteTask(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(
            TABLE_ROUTINES,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )

        db.close()
        return result
    }
}