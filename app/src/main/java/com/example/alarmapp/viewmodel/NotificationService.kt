package com.example.alarmapp.viewmodel

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.alarmapp.R
import com.example.alarmapp.models.Alarm
import com.example.alarmapp.models.Constants.Companion.ACTION_KILL
import com.example.alarmapp.models.Constants.Companion.ALARM_CODE
import com.example.alarmapp.models.Constants.Companion.BROADCAST_ALARM_CODE
import com.example.alarmapp.models.Constants.Companion.CHANNEL_ID
import com.example.alarmapp.models.Constants.Companion.KILL_CODE
import com.example.alarmapp.models.Constants.Companion.NOTI_SERVICE_TO_MAIN
import com.example.alarmapp.models.Constants.Companion.TAG
import com.example.alarmapp.models.Constants.Companion.TO_KILL_CODE
import com.example.alarmapp.models.Constants.Companion.TURN_OFF_SWITCH_ALARM_CODE
import com.example.alarmapp.view.MainActivity
import com.example.alarmapp.viewmodel.receivers.AlarmReceiver

class NotificationService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private var alarm: Alarm? = null
    private var kill = -1

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.extras

        if (bundle != null) {
            kill = bundle.getInt(TO_KILL_CODE, -1)

            //only get alarm from AlarmReceiver
            if (bundle.getSerializable(BROADCAST_ALARM_CODE) != null)
                alarm = bundle.getSerializable(BROADCAST_ALARM_CODE) as Alarm
        }

        //check received intent to stop alarm
        if (kill != -1){
            Log.i(TAG, "Stopping alarm")
            stopAlarm(this)}
        else {
            playAlarm(this)

            //intent to go to homepage
            val intent = Intent(this, MainActivity::class.java)
            val homePendingIntent = PendingIntent.getActivity(
                this,
                intent.hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            //notification layout
            val remoteView = RemoteViews(packageName, R.layout.notification_layout)
            remoteView.setTextViewText(R.id.noti_content, alarm?.getContent() ?: "Error")
            remoteView.setTextViewText(
                R.id.noti_title,
                "Alarming at ${alarm?.getHour()}:${alarm?.getMinute()}"
            )
            remoteView.setOnClickPendingIntent(R.id.noti_delete, pendingDeleteIntent(this))

            //create notification
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.alarm_icon)
                .setContentIntent(homePendingIntent)    //when press the notification, go to home page
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCustomContentView(remoteView)
                .setSound(null)
                .build()

            startForeground(notification.hashCode(), notification)
        }

        return START_NOT_STICKY
    }

    private fun playAlarm(context: Context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.ringtone)
        mediaPlayer?.start()

        //stop the service when alarm is done playing
        Handler().postDelayed({
            stopAlarm(context)
        }, mediaPlayer.duration.toLong())

        Log.i(TAG, "Alarm ${alarm?.getHour()}:${alarm?.getMinute()} played")
    }

    private fun stopAlarm(context: Context) {
        mediaPlayer?.release()

        //only switch off the alarm if it is not repeat, else just kill the music
        alarm?.let {
            if (!it.getRepeat()) {
                //local broadcast to viewmodel to turn off the switch
                val turnOffIntent = Intent(NOTI_SERVICE_TO_MAIN)
                val turnOffBundle = Bundle()
                turnOffBundle.putSerializable(TURN_OFF_SWITCH_ALARM_CODE, it)
                turnOffIntent.putExtras(turnOffBundle)

                Log.i(TAG, "Alarm ${it.getHour()}:${it.getMinute()} stopped")

                LocalBroadcastManager.getInstance(context).sendBroadcast(turnOffIntent)
            }
        }

        stopSelf()
        stopForeground(true)
    }

    private fun pendingDeleteIntent(context: Context): PendingIntent? {
        val deleteIntent = Intent(context, AlarmReceiver::class.java)

        val bundle = Bundle()
        bundle.putInt(KILL_CODE, ACTION_KILL)
        bundle.putSerializable(ALARM_CODE, alarm)
        deleteIntent.putExtras(bundle)

        return PendingIntent.getBroadcast(
            context,
            deleteIntent.hashCode(),
            deleteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        stopForeground(true)
    }
}