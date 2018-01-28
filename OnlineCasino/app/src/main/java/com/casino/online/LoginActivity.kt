package com.casino.online

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    private val LOGIN_URL = "http://ocwebapi.azurewebsites.net/api/logins"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun register(view: View) {
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
    }

    fun login(view: View) {
        val username = etLoginUsername.text.toString()
        val password = etLoginPassword.text.toString()

        val map = HashMap<String, String>()
        map.put("Content-Type", "application/json")

        val body = String.format("{ \"username\" : \"%s\", \"password\" : \"%s\" }", username, password)
        Fuel.post(LOGIN_URL).header(map).body(body).response { request, response, result ->
            result.success {
                val loginIntent = Intent(this, MenuActivity::class.java)
                loginIntent.putExtra("username", username)
                startActivity(loginIntent)
            }

            result.failure {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Login failed: " + response.responseMessage)
                        .setNegativeButton("Retry", null)
                        .create()
                        .show()
            }
        }
    }
}
