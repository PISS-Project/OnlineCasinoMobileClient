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

class RouletteActivity : AppCompatActivity() {

    private val reds = arrayOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)

    private val ROULETTE_BET_URL = "http://ocwebapi.azurewebsites.net/api/users/%s/roulettebets"

    private var imageRoulette: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roulette)
        imageRoulette = findViewById<View>(R.id.image_roulette) as ImageView
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
                val corner = 360 / 38 // corner for point
                val randPosition = corner * Random().nextInt(38) // random point
                val MIN = 5 // min rotation
                val MAX = 9 // max rotation
                val TIME_IN_WHEEL: Long = 1000  // time in one rotation
                val randRotation = MIN + Random().nextInt(MAX - MIN) // random rotation
                val truePosition = randRotation * 360 + randPosition
                val totalTime = TIME_IN_WHEEL * randRotation + TIME_IN_WHEEL / 360 * randPosition

                val randomInt = Random().nextInt(36)

                val animator = ObjectAnimator.ofFloat(view, "rotation", 0f, randomInt.toFloat())
                animator.setDuration(totalTime)
                animator.setInterpolator(DecelerateInterpolator())
                animator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {
                        imageRoulette!!.setEnabled(false)
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        imageRoulette!!.setEnabled(true)
                        val jsonObject = JSONObject(String(result.get()))
                        twRouletteWin.text = "You win " + jsonObject.getDouble("win").toString()
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
