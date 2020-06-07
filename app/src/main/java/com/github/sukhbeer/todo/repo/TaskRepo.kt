package com.github.sukhbeer.todo.repo

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.github.sukhbeer.todo.db.TodoDbHelper

class TaskRepo(private val dbHelper: TodoDbHelper) : BasicTaskRepo {

    override fun addTask(task: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(TodoDbHelper.TaskEntry.COLUMN_TASK_TITLE, task)
        db.insertWithOnConflict(TodoDbHelper.TaskEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    override fun deleteTask(task: String) {
        val db = dbHelper.writableDatabase
        db.delete(TodoDbHelper.TaskEntry.TABLE,
                TodoDbHelper.TaskEntry.COLUMN_TASK_TITLE + " = ?",
                arrayOf(task))
        db.close()
    }

    override fun findAll(): List<String> {
        val taskList = ArrayList<String>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(TodoDbHelper.TaskEntry.TABLE,
                arrayOf(TodoDbHelper.TaskEntry.ID, TodoDbHelper.TaskEntry.COLUMN_TASK_TITLE),
                null, null, null, null, null)

        while (cursor.moveToNext()) {
            val idx = cursor.getColumnIndex(TodoDbHelper.TaskEntry.COLUMN_TASK_TITLE)
            taskList.add(cursor.getString(idx))
        }

        cursor.close()
        db.close()

        return taskList
    }
}