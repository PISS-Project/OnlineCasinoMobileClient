package com.casino.online

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import kotlinx.android.synthetic.main.activity_deposit.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject

class DepositActivity : AppCompatActivity() {

    private val PUT_IN_WALLET_URL = "http://ocwebapi.azurewebsites.net/api/users/%s/wallet"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public override fun onSupportNavigateUp() : Boolean{
        finish();
        return true;
    }

    fun depositMoney(view: View) {
        val money = etMoney.text.toString()
        val token = intent.getStringExtra("token")
        val userId = intent.getStringExtra("userId")

        val header = HashMap<String, String>()
        header.put("Content-Type", "application/json")
        header.put("OnlineCasino-Token", token)

        val body = String.format("{ \"addMoney\" : \"%s\"}", money)
        val url = String.format(PUT_IN_WALLET_URL, userId)

        Fuel.put(url).header(header).body(body).response { request, response, result ->

            result.success {
                val jsonObject = JSONObject(String(result.get()))
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Money added. New balance: " + jsonObject.getString("newBalance"))
                        .setPositiveButton("OK", null)
                        .create()
                        .show()
            }

            result.failure {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Adding money failed: " + response.responseMessage)
                        .setNegativeButton("Retry", null)
                        .create()
                        .show()
            }

        }
    }
}
