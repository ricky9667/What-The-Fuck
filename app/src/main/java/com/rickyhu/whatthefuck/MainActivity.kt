package com.rickyhu.whatthefuck

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val WTF_URL = "https://yesno.wtf/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {

        val answerTextView = findViewById<TextView>(R.id.answerTextView)

        answerTextView.text = "?"
        answerTextView.setTextColor(resources.getColor(android.R.color.black))
        enableButtons(false)

        var wtfUrl = WTF_URL
        val button: Button = view as Button

        if (button.id != R.id.randomButton) {
            wtfUrl += ("?force=" + button.text)
        }

        val request = Request.Builder()
                .url(wtfUrl)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("WTF Data", "Load Failed")
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful)
                    throw IOException("Unexpected Code $response")

                val jsonString: String = response.body?.string() ?: "Response is null"
                Log.i("WTF json", jsonString)

                val json = JSONObject(jsonString)

                val answer: String = json.getString("answer")
                val forced: String = json.getString("forced")
                val imageUrl: String = json.getString("image")

                Log.i("WTF Answer", answer)
                Log.i("WTF Forced", forced)
                Log.i("WTF Image", imageUrl)

                runOnUiThread {
                    loadImage(imageUrl, answer)
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
                answerTextView.text = "Loading..."
                answerTextView.setTextColor(resources.getColor(android.R.color.black))
            }
        }
    }

    fun loadImage(url: String, answer: String) {
        val imageView = findViewById<ImageView>(R.id.imageView)
        Glide.with(this)
            .load(url)
            .centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    setResult(answer)
                    enableButtons(true)
                    return false
                }

            })
            .into(imageView)
    }

    fun enableButtons(enable: Boolean) {
        val yesButton = findViewById<Button>(R.id.yesButton)
        val maybeButton = findViewById<Button>(R.id.maybeButton)
        val noButton = findViewById<Button>(R.id.noButton)
        val randomButton = findViewById<Button>(R.id.randomButton)

        if (enable) {
            yesButton.isEnabled = true
            maybeButton.isEnabled = true
            noButton.isEnabled = true
            randomButton.isEnabled = true

            yesButton.isClickable = true
            maybeButton.isClickable = true
            noButton.isClickable = true
            randomButton.isClickable = true
        } else {
            yesButton.isEnabled = false
            maybeButton.isEnabled = false
            noButton.isEnabled = false
            randomButton.isEnabled = false


            yesButton.isClickable = false
            maybeButton.isClickable = false
            noButton.isClickable = false
            randomButton.isClickable = false
        }

    }
}