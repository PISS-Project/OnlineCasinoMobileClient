package com.casino.online

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import kotlinx.android.synthetic.main.activity_roulette.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class RouletteActivity : AppCompatActivity() {

    private val reds = arrayOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)

    private val ROULETTE_BET_URL = "http://ocwebapi.azurewebsites.net/api/users/%s/roulettebets"

    private var imageRoulette: ImageView? = null

    private var digits: HashMap<Int, Int>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roulette)
        imageRoulette = findViewById<View>(R.id.image_roulette) as ImageView
        initializeDigits()
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public override fun onSupportNavigateUp() : Boolean{
        finish();
        return true;
    }

    private fun initializeDigits() {
        digits = hashMapOf<Int, Int>(
                0 to 0,
                2 to 1,
                14 to 2,
                35 to 3,
                23 to 4,
                4 to 5,
                16 to 6,
                33 to 7,
                21 to 8,
                6 to 9,
                18 to 10,
                31 to 11,
                19 to 12,
                8 to 13,
                12 to 14,
                29 to 15,
                25 to 16,
                10 to 17,
                27 to 18,
                1 to 20,
                13 to 21,
                36 to 22,
                24 to 23,
                3 to 24,
                15 to 25,
                34 to 26,
                22 to 27,
                5 to 28,
                17 to 29,
                32 to 30,
                20 to 31,
                7 to 32,
                11 to 33,
                30 to 34,
                26 to 35,
                9 to 36,
                28 to 37
        )

    }


    fun makeRouletteBet(view: View) {
        val token = intent.getStringExtra("token")
        val userId = intent.getStringExtra("userId")

        val checked  = Arrays.toString(getChecked())

        val stake = etRouCredits.text.toString()
        
        val header = HashMap<String, String>()
        header.put("Content-Type", "application/json")
        header.put("OnlineCasino-Token", token)

        val body = "{ \"betValues\" :" + checked + ", \"stake\" : " + stake.toInt() + "}"
        val url = String.format(ROULETTE_BET_URL, userId)

        Fuel.post(url).header(header).body(body).response { request, response, result ->

            result.success {
                val jsonObject = JSONObject(String(result.get()))

                val corner = 360 / 38.00 // corner for point
                val spinResult = jsonObject.getInt("spinResult")

                val intPosition = digits!!.get(spinResult)!!.toInt()
                val position =  intPosition * corner
                val MIN = 5 // min rotation
                val MAX = 9 // max rotation
                val TIME_IN_WHEEL: Long = 1000  // time in one rotation
                val randRotation = MIN + Random().nextInt(MAX - MIN) // random rotation
                val truePosition = randRotation * 360 + position
                val totalTime = TIME_IN_WHEEL * randRotation + TIME_IN_WHEEL / 360 * position.toInt()

                val animator = ObjectAnimator.ofFloat(view, "rotation", 0f, truePosition.toFloat())
                animator.setDuration(totalTime)
                animator.setInterpolator(DecelerateInterpolator())
                animator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {
                        imageRoulette!!.setEnabled(false)
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        imageRoulette!!.setEnabled(true)

                        val returnedStake = jsonObject.getDouble("stake")
                        val winDigitString = jsonObject.getDouble("win")
                        if (winDigitString.compareTo(0.0) == 0) {
                            twRouletteWin.text = String.format("Result is %2d. You lost %.0f credits!", spinResult, returnedStake)
                        } else {
                            twRouletteWin.text = String.format("Result is %2d. You win %.0f credits!", spinResult, winDigitString)
                        }

                    }

                    override fun onAnimationCancel(animator: Animator) {

                    }

                    override fun onAnimationRepeat(animator: Animator) {

                    }
                })
                animator.start()
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


    fun select(view: View) {
        val whiteColor = "0xffff00"
        val yellowColor = "0xffffff"

        val button = findViewById<Button>(view.id)

        val color = button.textColors.defaultColor

        if (color == Color.YELLOW){
            button.setTextColor(Color.WHITE)
        } else if (color == Color.WHITE)
            button.setTextColor(Color.YELLOW)
    }

    fun onlyBlacks(view: View) {
        for (i in 0 .. 36){
            val id = resources.getIdentifier("btn" + i, "id", packageName)
            val button = findViewById<Button>(id)
            if ( !reds.contains(i) && i != 0) {
                button.setTextColor(Color.YELLOW)
            } else {
                button.setTextColor(Color.WHITE)
            }
        }
    }

    fun onlyReds(view: View) {
        for (i in 0 .. 36){
            val id = resources.getIdentifier("btn" + i, "id", packageName)
            val button = findViewById<Button>(id)
            if ( reds.contains(i) && i != 0) {
                button.setTextColor(Color.YELLOW)
            } else {
                button.setTextColor(Color.WHITE)
            }
        }
    }

    fun getChecked() : Array<Int> {
        val list = ArrayList<Int>()
        for (i in 0 .. 36){
            val id = resources.getIdentifier("btn" + i, "id", packageName)
            val button = findViewById<Button>(id)
            val color = button.textColors.defaultColor

            if (color == Color.YELLOW) {
                list.add(i)
            }
        }

        return list.toTypedArray()
    }
}
