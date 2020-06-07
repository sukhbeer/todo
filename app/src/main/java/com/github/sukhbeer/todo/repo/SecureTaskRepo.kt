package com.github.sukhbeer.todo.repo

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.github.sukhbeer.todo.db.TodoDbHelper
import com.scottyab.aescrypt.AESCrypt


class SecureTaskRepo(private val dbHelper: TodoDbHelper, private val password: String) : BasicTaskRepo {

    override fun addTask(task: String) {
        val encrypted = AESCrypt.encrypt(password, task)

        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(TodoDbHelper.SecureTaskEntry.COLUMN_TASK_TITLE, encrypted)
        db.insertWithOnConflict(TodoDbHelper.SecureTaskEntry.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    override fun deleteTask(task: String) {
        val encrypted = AESCrypt.encrypt(password, task)

        val db = dbHelper.writableDatabase
        db.delete(TodoDbHelper.SecureTaskEntry.TABLE,
                TodoDbHelper.SecureTaskEntry.COLUMN_TASK_TITLE + " = ?",
                arrayOf(encrypted))
        db.close()
    }

    override fun findAll(): List<String> {
        val taskList = ArrayList<String>()

        val db = dbHelper.readableDatabase
        val cursor = db.query(TodoDbHelper.SecureTaskEntry.TABLE,
                arrayOf(TodoDbHelper.SecureTaskEntry.ID, TodoDbHelper.SecureTaskEntry.COLUMN_TASK_TITLE),
                null, null, null, null, null)

        while (cursor.moveToNext()) {
            val idx = cursor.getColumnIndex(TodoDbHelper.SecureTaskEntry.COLUMN_TASK_TITLE)
            taskList.add(AESCrypt.decrypt(password, cursor.getString(idx)))
        }

        cursor.close()
        db.close()
        return taskList
    }

    fun deleteAll() {
        val db = dbHelper.writableDatabase
        db.execSQL("delete from  ${TodoDbHelper.SecureTaskEntry.TABLE}")
        db.close()
    }
}