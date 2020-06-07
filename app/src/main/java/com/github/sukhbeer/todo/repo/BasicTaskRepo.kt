package com.github.sukhbeer.todo.repo

interface BasicTaskRepo {
    fun addTask(task: String)

    fun deleteTask(task: String)

    fun findAll(): List<String>
}