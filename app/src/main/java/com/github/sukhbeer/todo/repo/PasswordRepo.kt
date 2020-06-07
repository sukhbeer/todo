package com.github.sukhbeer.todo.repo

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import at.favre.lib.crypto.bcrypt.BCrypt
import com.github.sukhbeer.todo.db.TodoDbHelper


class PasswordRepo(private val dbHelper: TodoDbHelper) {

    fun deletePassword() {
        val db = dbHelper.writableDatabase
        db.execSQL("delete from  ${TodoDbHelper.Password.TABLE}")
        db.close()
    }

    fun savePassword(password: String) {
        val bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray())

        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(TodoDbHelper.Password.COLUMN_TASK_PASSWORD, bcryptHashString)
        db.insertWithOnConflict(TodoDbHelper.Password.TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun checkIfPasswordExists(): Boolean {

        val db = dbHelper.readableDatabase
        val cursor = db.query(TodoDbHelper.Password.TABLE,
                arrayOf(TodoDbHelper.Password.ID, TodoDbHelper.Password.COLUMN_TASK_PASSWORD),
                null, null, null, null, null)

        try {
            if (cursor.moveToNext()) {
                return true
            }
        } finally {
            cursor.close()
            db.close()
        }

        return false
    }

    fun checkPassword(password: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TodoDbHelper.Password.TABLE,
                arrayOf(TodoDbHelper.Password.ID, TodoDbHelper.Password.COLUMN_TASK_PASSWORD),
                null, null, null, null, null)

        try {
            if (cursor.moveToNext()) {
                val idx = cursor.getColumnIndex(TodoDbHelper.Password.COLUMN_TASK_PASSWORD)
                val bcryptHashString = cursor.getString(idx)
                val result = BCrypt.verifyer().verify(password.toCharArray(), bcryptHashString)

                return result.verified
            }
        } finally {
            cursor.close()
            db.close()
        }

        return false
    }
}