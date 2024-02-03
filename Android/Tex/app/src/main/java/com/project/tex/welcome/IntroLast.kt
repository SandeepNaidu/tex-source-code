package com.project.tex.welcome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.project.tex.R
import com.project.tex.emailAuth.LoginActivity
import com.project.tex.menu.PrivacyActivity
import com.project.tex.menu.TermsActivity
import com.project.tex.onboarding.login.QuickSignInActivity

class IntroLast : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_last)
        val email = findViewById<Button>(R.id.next)
        val phone = findViewById<Button>(R.id.phone)
        email.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    LoginActivity::class.java
                )
            )
        }
        phone.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    QuickSignInActivity::class.java
                )
            )
        }
        val terms = findViewById<TextView>(R.id.terms)
        val privacy = findViewById<TextView>(R.id.privacy)
        privacy.setOnClickListener {
            startActivity(
                Intent(
                    this@IntroLast,
                    PrivacyActivity::class.java
                )
            )
        }
        terms.setOnClickListener {
            startActivity(
                Intent(
                    this@IntroLast,
                    TermsActivity::class.java
                )
            )
        }
    }
}