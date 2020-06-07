package com.github.sukhbeer.todo


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.method.PasswordTransformationMethod
//import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.github.sukhbeer.todo.db.TodoDbHelper
import com.github.sukhbeer.todo.repo.PasswordRepo
import com.github.sukhbeer.todo.repo.SecureTaskRepo


class MainActivity : AppCompatActivity() {
    private var todoButton: Button? = null
    private var secretButton: Button? = null
    private var changePassButton: Button? = null
    private lateinit var passwordRepo: PasswordRepo
    private lateinit var secureTaskRepo: SecureTaskRepo
    private lateinit var todoDbHelper: TodoDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoDbHelper = TodoDbHelper(this)
        passwordRepo = PasswordRepo(todoDbHelper)

        todoButton = findViewById(R.id.todo_button)
        todoButton!!.setOnClickListener { openTodo() }

        secretButton = findViewById(R.id.secret_button)
        secretButton!!.setOnClickListener { secretTodo() }

        changePassButton = findViewById(R.id.change_password_button)
        changePassButton!!.setOnClickListener { changePass() }
    }

    private fun changePass() {
        val taskEditText = EditText(this)
        taskEditText.transformationMethod = PasswordTransformationMethod()

        if (!passwordRepo.checkIfPasswordExists()) {
            createNewPass(taskEditText)
        } else {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL
            val newPasswordBox = EditText(this)
            val newPasswordBoxAgain = EditText(this)

            newPasswordBox.transformationMethod = PasswordTransformationMethod()
            newPasswordBoxAgain.transformationMethod = PasswordTransformationMethod()

            taskEditText.hint = "Old Password"
            newPasswordBox.hint = "New Password"
            newPasswordBoxAgain.hint = "New Password Again"

            linearLayout.addView(taskEditText)
            linearLayout.addView(newPasswordBox)
            linearLayout.addView(newPasswordBoxAgain)

            AlertDialog.Builder(this)
                    .setTitle("Enter NEW password")
                    .setMessage("Minimum 8 characters")
                    .setView(linearLayout)
                    .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        val password = taskEditText.text.toString()
                        val newPassword = newPasswordBox.text.toString()
                        val newPasswordAgain = newPasswordBoxAgain.text.toString()

                        if (!passwordRepo.checkPassword(password)) {
                            showDialog(this, "Error", "incorrect old password")
                        } else if (newPassword.length < 8) {
                            showDialog(this, "Error", "new password is too short")
                        } else if (newPassword != newPasswordAgain) {
                            showDialog(this, "Error", "passwords don't match")
                        } else {
                            //first decrypt all the tasks using the old password
                            val findAll = SecureTaskRepo(todoDbHelper, password).findAll()

                            //then delete all the tasks
                            SecureTaskRepo(todoDbHelper, password).deleteAll()

                            //then save them again with the new password
                            secureTaskRepo = SecureTaskRepo(todoDbHelper, newPassword)
                            for (task in findAll) {
                                secureTaskRepo.addTask(task)
                            }

                            passwordRepo.deletePassword()
                            passwordRepo.savePassword(newPassword)
                            showDialog(this, "Info", "Password changed successfully")
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create().show()
        }
    }

    private fun openTodo() {
        val intent = Intent(this, TodoActivity::class.java)
        startActivity(intent)
    }

    private fun secretTodo() {

        val taskEditText = EditText(this)
        taskEditText.transformationMethod = PasswordTransformationMethod()

        if (!passwordRepo.checkIfPasswordExists()) {
            createNewPass(taskEditText)
        } else {
            val dialog = AlertDialog.Builder(this)
                    .setTitle("Enter password")
                    .setMessage("Enter password")
                    .setView(taskEditText)
                    .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->

                        //check password
                        val password = taskEditText.text.toString()

                        if (!passwordRepo.checkPassword(password)) {
                            showDialog(this, "Error", "invalid password")
                        } else {
                            val intent = Intent(this, SecureTodoActivity::class.java)
                            intent.putExtra("password", password)
                            startActivity(intent)
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
            dialog.show()
        }
    }

    private fun createNewPass(taskEditText: EditText) {
        AlertDialog.Builder(this)
                .setTitle("Enter NEW password")
                .setMessage("Minimum 8 characters")
                .setView(taskEditText)
                .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    val password = taskEditText.text.toString()

                    if (password.length < 8) {
                        showDialog(this, "Error", "password is too short")
                    } else {
                        passwordRepo.savePassword(password)
                    }
                })
                .setNegativeButton("Cancel", null)
                .create().show()
    }
}
