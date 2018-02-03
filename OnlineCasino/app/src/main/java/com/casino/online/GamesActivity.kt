package com.casino.online

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_games.*

class GamesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public override fun onSupportNavigateUp() : Boolean{
        finish();
        return true;
    }


    fun playDices(view: View) {
        val token = intent.getStringExtra("token")
        val userId = intent.getStringExtra("userId")
        val intent = Intent(this, DicesActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    fun playRoulette(view: View) {
        val token = intent.getStringExtra("token")
        val userId = intent.getStringExtra("userId")
        val intent = Intent(this, RouletteActivity::class.java)
        intent.putExtra("token", token)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}
