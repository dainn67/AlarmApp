package com.example.alarmapp.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.alarmapp.models.Alarm
import com.example.alarmapp.models.Constants.Companion.ALARM_CODE
import com.example.alarmapp.models.Constants.Companion.TAG
import com.example.alarmapp.viewmodel.receivers.AlarmReceiver
import java.util.Calendar

class AlarmScheduler(
    private val context: Context
) : IAlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    companion object{
        fun convertToMillis(hour: Int, minute: Int): Long {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            return calendar.timeInMillis
        }

        fun hashcodeAlarm(alarm: Alarm): Int{
            return convertToMillis(alarm.getHour(), alarm.getMinute()).toInt()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun schedule(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val bundle = Bundle()
        bundle.putSerializable(ALARM_CODE, alarm)
        intent.putExtras(bundle)

        //schedule the alarm and trigger the pending intent
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            convertToMillis(alarm.getHour(), alarm.getMinute()),
            PendingIntent.getBroadcast(
                context,
                hashcodeAlarm(alarm),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

//        val dayInMillis: Long = 24L * 60 * 60 * 1000
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            convertToMillis(alarm.getHour(), alarm.getMinute()),
//            dayInMillis,
//            PendingIntent.getBroadcast(
//                context,
//                hashcodeAlarm(alarm),
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        )
    }

    override fun cancel(alarm: Alarm) {
        //same pending intent when setting the alarm
        val intent = Intent(context, AlarmReceiver::class.java)
        val bundle = Bundle()
        bundle.putSerializable(ALARM_CODE, alarm)
        intent.putExtras(bundle)

        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                hashcodeAlarm(alarm),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        val stopNotiIntent = Intent(context, NotificationService::class.java)
        context.stopService(stopNotiIntent)

        Log.i(TAG, "Cancel $alarm")
    }
}