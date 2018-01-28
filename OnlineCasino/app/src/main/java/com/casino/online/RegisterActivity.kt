package com.casino.online

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val REGISTER_USER_URL = "http://ocwebapi.azurewebsites.net/api/users"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }


    fun register(view: View) {
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()
        val fullName = etFullName.text.toString()
        val email = etEmail.text.toString()

        val map = HashMap<String, String>()
        map.put("Content-Type", "application/json")

        val body = String.format("{ \"username\" : \"%s\", \"password\" : \"%s\", \"fullName\" : \"%s\", \"email\" : \"%s\" }", username, password, fullName, email)
        Fuel.post(REGISTER_USER_URL).header(map).body(body).response { request, response, result ->
            result.success {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }

            result.failure {
                val builder = AlertDialog.Builder(this)
                    builder.setMessage("Registration failed!")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show()
            }
        }

    }
}
