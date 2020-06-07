package com.github.sukhbeer.todo.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class TodoDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val createTableTask = "CREATE TABLE ${TaskEntry.TABLE}  ( " +
            "${TaskEntry.ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${TaskEntry.COLUMN_TASK_TITLE} TEXT NOT NULL);"

    private val createTableSecureTask = "CREATE TABLE ${SecureTaskEntry.TABLE}  ( " +
            "${SecureTaskEntry.ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${SecureTaskEntry.COLUMN_TASK_TITLE} TEXT NOT NULL);"

    private val createTablePassword = "CREATE TABLE ${Password.TABLE}  ( " +
            "${Password.ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${Password.COLUMN_TASK_PASSWORD} TEXT NOT NULL);"

    private val dropTableTask = "DROP TABLE IF EXISTS ${TaskEntry.TABLE}"
    private val dropTableSecureTask = "DROP TABLE IF EXISTS ${SecureTaskEntry.TABLE}"
    private val dropTablePassword = "DROP TABLE IF EXISTS ${Password.TABLE}"

    companion object {
        const val DATABASE_VERSION = 4
        const val DATABASE_NAME = "todo.db"
    }

    object Password : BaseColumns {
        const val TABLE = "password"
        const val COLUMN_TASK_PASSWORD = "pass"
        const val ID = BaseColumns._ID
    }

    object TaskEntry : BaseColumns {
        const val TABLE = "task"
        const val COLUMN_TASK_TITLE = "title"
        const val ID = BaseColumns._ID
    }

    object SecureTaskEntry : BaseColumns {
        const val TABLE = "secure_task"
        const val COLUMN_TASK_TITLE = "title"
        const val ID = BaseColumns._ID
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableTask)
        db.execSQL(createTableSecureTask)
        db.execSQL(createTablePassword)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(dropTableTask)
        db.execSQL(dropTableSecureTask)
        db.execSQL(dropTablePassword)
        onCreate(db)
    }
}