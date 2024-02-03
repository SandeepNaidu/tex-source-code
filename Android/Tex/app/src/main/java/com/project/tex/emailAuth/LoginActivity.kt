package com.project.tex.emailAuth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.tex.R
import com.project.tex.firebase.FirebaseDBManager
import com.project.tex.main.HomeActivity
import com.project.tex.onboarding.signup.SignUpActivity
import java.util.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Firebase
        val mAuth = FirebaseAuth.getInstance()

        //Back
        findViewById<View>(R.id.imageView).setOnClickListener { v: View? -> onBackPressed() }

        //OnClick
        findViewById<View>(R.id.signUP).setOnClickListener { v: View? ->
            startActivity(
                Intent(
                    this@LoginActivity,
                    SignUpActivity::class.java
                )
            )
        }

        //EditText
        val email = findViewById<EditText>(R.id.email)
        val pass = findViewById<EditText>(R.id.pass)

        //Button
        findViewById<View>(R.id.login).setOnClickListener { v: View? ->
            findViewById<View>(R.id.progressBar).visibility = View.VISIBLE
            val mEmail = email.text.toString().trim { it <= ' ' }
            val mPassword = pass.text.toString().trim { it <= ' ' }
            if (mEmail.isEmpty()) {
                Snackbar.make(v!!, "Enter your email", Snackbar.LENGTH_LONG).show()
                findViewById<View>(R.id.progressBar).visibility = View.INVISIBLE
            } else if (mPassword.isEmpty()) {
                Snackbar.make(v!!, "Enter your password", Snackbar.LENGTH_LONG).show()
                findViewById<View>(R.id.progressBar).visibility = View.INVISIBLE
            } else {
                mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            FirebaseDBManager.dbInstance.getReference("Users")
                                .child((Objects.requireNonNull(FirebaseAuth.getInstance().currentUser) as FirebaseUser).uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            val intent =
                                                Intent(this@LoginActivity, HomeActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            FirebaseAuth.getInstance().currentUser!!.delete()
                                            Snackbar.make(
                                                v!!,
                                                "User doesn't exist",
                                                Snackbar.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                        } else {
                            val msg = (Objects.requireNonNull(task.exception) as Exception).message!!
                            Snackbar.make(v!!, msg, Snackbar.LENGTH_LONG).show()
                        }
                        findViewById<View>(R.id.progressBar).visibility = View.INVISIBLE
                    }
            }
        }
    }
}