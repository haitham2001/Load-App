package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.notifications.createChannel
import com.udacity.notifications.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var selectedUri: String = NONE_URL
    private lateinit var notificationStatus: String
    private lateinit var notificationMessage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel),
            application
        )
        val urlPattern = Pattern.compile("https://.+?\\.com(/.+?)*?")

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radioGroupId.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.customURLBtnId)
                editTextURL.isClickable = true
            else
            {
                editTextURL.isClickable = false
                editTextURL.setText("")
            }
        }

        instructions.setOnClickListener {
            Toast.makeText(applicationContext,"starts with https:// and the web name ends with .com followed by the endpoints", Toast.LENGTH_LONG).show()
        }

        custom_button.setOnClickListener {
            when(radioGroupId.checkedRadioButtonId){
                R.id.loadAppBtnId -> {
                    selectedUri = LOAD_APP_URL
                    notificationMessage = getString(R.string.load_app_radio_text)
                }
                R.id.glideBtnId -> {
                    selectedUri = GLIDE_URL
                    notificationMessage = getString(R.string.glide_radio_text)
                }
                R.id.retrofitBtnId ->{
                    selectedUri = RETROFIT_URL
                    notificationMessage = getString(R.string.retrofit_radio_text)
                }
                R.id.customURLBtnId -> {
                    if(editTextURL.text.toString().isNotEmpty()
                        && urlPattern.matcher(editTextURL.text.toString()).matches())
                    {
                        selectedUri = editTextURL.text.toString()
                        notificationMessage = "Custom URL"
                    }
                    else {
                        selectedUri = NONE_URL
                        Toast.makeText(applicationContext,"Please enter a valid URL", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    selectedUri = NONE_URL
                    Toast.makeText(this, "Please Select one of the items", Toast.LENGTH_SHORT).show()
                }
            }
            download(selectedUri)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val notification: NotificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            )as NotificationManager
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val download = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor: Cursor = download.query(
                DownloadManager
                    .Query()
                    .setFilterById(id!!)
            )

            if (cursor.moveToNext()) {
                val req = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                notificationStatus =
                    if (req == DownloadManager.STATUS_SUCCESSFUL)
                        getString(R.string.success_status)
                    else
                        getString(R.string.failed_status)

                notification.sendNotification(notificationMessage,context!!,notificationStatus)
            }
            custom_button.buttonState = ButtonState.Completed
        }
    }

    private fun download(chosenUri: String) {
        if(chosenUri != NONE_URL)
        {
            custom_button.buttonState = ButtonState.Loading
            val request =
                DownloadManager.Request(Uri.parse(chosenUri))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)
        }
    }

    companion object {
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit"
        private const val NONE_URL =
            "None"
    }

}
