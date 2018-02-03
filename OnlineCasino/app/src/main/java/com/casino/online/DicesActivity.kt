package com.casino.online

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import kotlinx.android.synthetic.main.activity_dices.*
import org.json.JSONObject

import java.util.Random

class DicesActivity : AppCompatActivity() {

    private val bets = arrayOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");

    private val BET_URL = "http://ocwebapi.azurewebsites.net/api/users/%s/dicebets"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dices)
        val rollDices = findViewById<View>(R.id.rollDices) as Button
        val imageView1 = findViewById<View>(R.id.imageView1) as ImageView
        val imageView2 = findViewById<View>(R.id.imageView2) as ImageView
        val token = intent.getStringExtra("token")
        val userId = intent.getStringExtra("userId")

        rollDices!!.setOnClickListener {
            makeBet(token, userId)
        }

        applySpinnerAdapter()
    }

    fun setResultString(actualRoll: Int, win: Double) {
        textView3.text = String.format("Sum of dices: %2d. You won %.0f credits!", actualRoll, win)
    }

    fun applySpinnerAdapter() {
        val adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, bets)
        val spinner: MaterialBetterSpinner = findViewById (R.id.spinnerBet)
        spinner.setAdapter(adapter)
    }

    companion object {

        val RANDOM = Random()

        fun randomDiceValue(actualRoll: Int): Int {
            return RANDOM.nextInt(actualRoll - 2) + 1
        }
    }

    fun makeBet(token: String, userId: String)  {
        val spinnerBet: MaterialBetterSpinner = findViewById(R.id.spinnerBet)
        val bet = spinnerBet.text.toString().toInt()
        val editText3: EditText = findViewById(R.id.editText3)
        val stake = editText3.text.toString().toInt()

        val header1 = HashMap<String, String>()
        header1.put("Content-Type", "application/json")
        header1.put("OnlineCasino-Token", token)

        val body = String.format("{ \"bet\" : %2d, \"stake\" : %2d }", bet, stake)
        val url = String.format(BET_URL, userId)

        Fuel.post(url).header(header1).body(body).response { request, response, result ->

            result.success {
                val jsonResponse = JSONObject(String(result.get()))
                val win = jsonResponse.getDouble("win")
                val actualRoll = jsonResponse.getInt("actualRoll")
                val anim2 = AnimationUtils.loadAnimation(this@DicesActivity, R.anim.shake)
                val anim1 = AnimationUtils.loadAnimation(this@DicesActivity, R.anim.shake)

                var firstDice = 0
                var secondDice = 0

                if (actualRoll <= 7) {
                    firstDice = randomDiceValue(actualRoll)
                    secondDice = actualRoll - firstDice
                } else {
                    val startBound = Math.abs(6 - actualRoll)
                    firstDice = startBound + Random().nextInt(6 - startBound)
                    secondDice = actualRoll - firstDice
                }

                val animationListener = object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        val res = resources.getIdentifier("dice_" + firstDice, "drawable", "com.casino.online")

                        imageView1!!.setImageResource(res)
                        setResultString(actualRoll, win)
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                }

                val animationListener2 = object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        val res = resources.getIdentifier("dice_" + secondDice, "drawable", "com.casino.online")

                        imageView2!!.setImageResource(res)
                        setResultString(actualRoll, win)
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                }

                anim1.setAnimationListener(animationListener)
                anim2.setAnimationListener(animationListener2)

                imageView1!!.startAnimation(anim1)
                imageView2!!.startAnimation(anim2)

            }

            result.failure {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Betting failed: " + response.responseMessage)
                        .setNegativeButton("Retry", null)
                        .create()
                        .show()
            }

        }

    }
}