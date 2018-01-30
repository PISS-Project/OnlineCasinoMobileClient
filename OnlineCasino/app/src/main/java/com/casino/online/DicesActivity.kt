package com.casino.online

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView

import java.util.Random

class DicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dices)
        val rollDices = findViewById<View>(R.id.rollDices) as Button
        val imageView1 = findViewById<View>(R.id.imageView1) as ImageView
        val imageView2 = findViewById<View>(R.id.imageView2) as ImageView

        rollDices!!.setOnClickListener {
            val anim1 = AnimationUtils.loadAnimation(this@DicesActivity, R.anim.shake)
            val anim2 = AnimationUtils.loadAnimation(this@DicesActivity, R.anim.shake)
            val animationListener = object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    val value = randomDiceValue()
                    val res = resources.getIdentifier("dice_" + value, "drawable", "com.casino.online")

                    if (animation === anim1) {
                        imageView1!!.setImageResource(res)
                    } else if (animation === anim2) {
                        imageView2!!.setImageResource(res)
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            }

            anim1.setAnimationListener(animationListener)
            anim2.setAnimationListener(animationListener)

            imageView1!!.startAnimation(anim1)
            imageView2!!.startAnimation(anim2)
        }
    }

    companion object {

        val RANDOM = Random()

        fun randomDiceValue(): Int {
            return RANDOM.nextInt(6) + 1
        }
    }
}