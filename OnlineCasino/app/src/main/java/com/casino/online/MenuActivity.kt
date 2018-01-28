package com.casino.online

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        showWelcomeMessage()
    }

    fun showWelcomeMessage() {
        val username = intent.getStringExtra("username")
        twWelcome.text = String.format("Welcome, %s!", username)
    }
}
