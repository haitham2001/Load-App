package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.notifications.cancelNotifications
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        //Remove the notification after interacting with it
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()

        //Set the content we got from main
        val body = intent.getStringExtra("Message")
        val status = intent.getStringExtra("CurrentStatus")
        fileNameId.text = body
        statusId.text = status

        //Set the image depending on the status
        statusImageId.setImageResource(
            if (status == getString(R.string.success_status))
                R.drawable.clipart3011314
            else
                R.drawable.clipart2210076
        )

        //Button to go back to the main menu
        backToMainBtnId.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}
