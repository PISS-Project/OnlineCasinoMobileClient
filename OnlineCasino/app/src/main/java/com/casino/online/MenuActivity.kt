package com.casino.online

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.view.View
import com.casino.online.R.id.twWelcome
import kotlinx.android.synthetic.main.activity_menu.*
import kotlin.system.exitProcess

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        showWelcomeMessage()
    }

    @Override
    public override fun onSupportNavigateUp() : Boolean{
        finish();
        return true;
    }

    private fun showWelcomeMessage() {
        val username = intent.getStringExtra("username")
        twWelcome.text = String.format("Welcome, %s!", username)
    }

    fun logout(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun play(view: View) {
        val token = intent.getStringExtra("token")
        val userId = intent.getStringExtra("userId")
        val intent = Intent(this, GamesActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    fun viewProfile(view: View) {
        val token = intent.getStringExtra("token")
        val userId = intent.getStringExtra("userId")
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}
