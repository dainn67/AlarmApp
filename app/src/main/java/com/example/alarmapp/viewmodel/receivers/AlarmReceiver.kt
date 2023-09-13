package com.example.alarmapp.viewmodel.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.alarmapp.models.Alarm
import com.example.alarmapp.models.Constants.Companion.ALARM_CODE
import com.example.alarmapp.models.Constants.Companion.BROADCAST_ALARM_CODE
import com.example.alarmapp.models.Constants.Companion.KILL_CODE
import com.example.alarmapp.models.Constants.Companion.TO_KILL_CODE
import com.example.alarmapp.viewmodel.AlarmScheduler
import com.example.alarmapp.viewmodel.NotificationService
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras

        //receive alarm and kill action if exist
        val alarm = bundle?.getSerializable(ALARM_CODE) as Alarm?
        val kill = bundle?.getInt(KILL_CODE, -1)

        //intent and bundle to start notification
        val sendToNotificationIntent = Intent(context, NotificationService::class.java)
        val sendToNotificationBundle = Bundle()

        if (kill == 1)
            sendToNotificationBundle.putInt(TO_KILL_CODE, kill)
        if (alarm != null)
            sendToNotificationBundle.putSerializable(BROADCAST_ALARM_CODE, alarm)

        sendToNotificationIntent.putExtras(sendToNotificationBundle)

        //start the notification
        context?.startService(sendToNotificationIntent)

        //set the next alarm
        if (kill != 1 && alarm != null && alarm.getRepeat()) {
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val newAlarmIntent = Intent(context, AlarmReceiver::class.java)

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 1)

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                PendingIntent.getBroadcast(
                    context,
                    AlarmScheduler.hashcodeAlarm(alarm),
                    newAlarmIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }
}