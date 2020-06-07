package com.github.sukhbeer.todo

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.github.sukhbeer.todo.repo.BasicTaskRepo


abstract class BaseTodoActivity : AppCompatActivity() {
    protected lateinit var taskRepo: BasicTaskRepo
    protected lateinit var mTaskListView: ListView
    protected var mAdapter: ArrayAdapter<String>? = null

    fun setRepo(repo: BasicTaskRepo) {
        taskRepo = repo
    }

    fun deleteTask(view: View) {
        val parent = view.parent as View
        val taskTextView = parent.findViewById<TextView>(R.id.task_title)

        taskRepo.deleteTask(taskTextView.text.toString())

        updateUI()
    }

    protected fun updateUI() {
        val taskList = taskRepo.findAll()

        if (mAdapter == null) {
            mTaskListView.adapter = ArrayAdapter(
                this,
                R.layout.item_todo,
                R.id.task_title,
                taskList
            )
        } else {
            mAdapter?.clear()
            mAdapter?.addAll(taskList)
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_task -> {
                val taskEditText = EditText(this)
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Add a new task")
                    .setMessage("What is your next task?")
                    .setView(taskEditText)
                    .setPositiveButton("Add", DialogInterface.OnClickListener { dialog, which ->
                        taskRepo.addTask(taskEditText.text.toString())
                        updateUI()
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                dialog.show()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

}