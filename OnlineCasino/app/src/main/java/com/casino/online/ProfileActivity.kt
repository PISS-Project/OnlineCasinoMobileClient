package com.casino.online

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.animation.AnimationUtils
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    private val USER_INFO_URL = "http://ocwebapi.azurewebsites.net/api/users/%s"

    private val CHECK_WALLET_URL = "http://ocwebapi.azurewebsites.net/api/users/%s/wallet"

    private val PROFILE_URL = "http://ocwebapi.azurewebsites.net/api/users/%s/profile"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        showProfileInfo()
    }

    private fun showProfileInfo() {
        val token = intent.getStringExtra("token")
        val userId = intent.getStringExtra("userId")

        val header = HashMap<String, String>()
        header.put("OnlineCasino-Token", token)

        val url = String.format(USER_INFO_URL, userId)

        Fuel.get(url).header(header).response { request, response, result ->

            result.success {
                val jsonObject = JSONObject(String(result.get()))
                etPUsername.setText(jsonObject.getString("username"))
                etPFullname.setText(jsonObject.getString("fullName"))
                etPEmail.setText(jsonObject.getString("email"))

                val header = HashMap<String, String>()
                header.put("OnlineCasino-Token", token)

                val url = String.format(CHECK_WALLET_URL, userId)

                Fuel.get(url).header(header).response { request, response, result ->

                    result.success {
                        val jsonObject = JSONObject(String(result.get()))
                        etPWallet.setText(jsonObject.getString("balance"))
                    }
                }
            }

            result.failure {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Getting profile info failed: " + response.responseMessage)
                        .setNegativeButton("Retry", null)
                        .create()
                        .show()
            }

        }

    }

    fun deposit(view: View) {
        val depositIntent = Intent(this, DepositActivity::class.java)
        val token = intent.getStringExtra("token")
        val userId = intent.getStringExtra("userId")
        val credits = etPWallet.text.toString()
        depositIntent.putExtra("token", token)
        depositIntent.putExtra("userId", userId)
        depositIntent.putExtra("credits", credits)

        startActivity(depositIntent)
    }

    fun save(view: View) {
        val token = intent.getStringExtra("token")
        val userId = intent.getStringExtra("userId")

        val email = etPEmail.text.toString()
        val fullname = etPFullname.text.toString()


        val header = HashMap<String, String>()
        header.put("Content-Type", "application/json")
        header.put("OnlineCasino-Token", token)

        val body = String.format("{ \"fullName\" : \"%s\", \"email\" : \"%s\" }", fullname, email)
        val url = String.format(PROFILE_URL, userId)

        Fuel.put(url).header(header).body(body).response { request, response, result ->

            result.success {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Saving profile successfully: ")
                        .setPositiveButton("OK", null)
                        .create()
                        .show()
            }

            result.failure {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Saving profile info failed: " + response.responseMessage)
                        .setNegativeButton("Retry", null)
                        .create()
                        .show()
            }

        }
    }
}
