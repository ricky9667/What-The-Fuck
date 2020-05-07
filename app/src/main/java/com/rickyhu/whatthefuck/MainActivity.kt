package com.rickyhu.whatthefuck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val WTF_URL = "https://yesno.wtf/api"
    var FORCE = "?="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        val request = Request.Builder()
                .url(WTF_URL)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("WTF Data", "Load Failed")
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful)
                        throw IOException("Unexpected Code $response")

                    val jsonString: String = response.body?.string() ?: "Response is null"
                    Log.i("WTF json", jsonString)

                    val json = JSONObject(jsonString)
//                    Log.i("WTF Data", json.toString())

                    val answer: String = json.getString("answer")
                    val forced: String = json.getString("forced")
                    val imageUrl: String = json.getString("image")

                    Log.i("WTF Answer", answer)
                    Log.i("WTF Forced", forced)
                    Log.i("WTF Image", imageUrl)

                    runOnUiThread {
                        setResult(answer)
                    }
                }
            }
        })
    }

    fun setResult(answer: String) {
        val answerTextView = findViewById<TextView>(R.id.answerTextView)

        when (answer) {
            "yes" -> {
                answerTextView.text = "Yes"
                answerTextView.setTextColor(resources.getColor(android.R.color.holo_green_light))
            }

            "maybe" -> {
                answerTextView.text = "Maybe"
                answerTextView.setTextColor(resources.getColor(android.R.color.holo_orange_light))
            }

            "no" -> {
                answerTextView.text = "No"
                answerTextView.setTextColor(resources.getColor(android.R.color.holo_red_light))
            }

            else -> {
                answerTextView.text = "Loading"
                answerTextView.setTextColor(resources.getColor(android.R.color.black))
            }

        }
    }

}