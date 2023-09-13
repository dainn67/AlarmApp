package com.example.alarmapp.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alarmapp.models.Alarm
import com.example.alarmapp.models.Constants
import com.example.alarmapp.models.Constants.Companion.DB_NAME
import com.example.alarmapp.models.Constants.Companion.TAG
import com.example.alarmapp.models.Data

class MyViewModel(
    private val context: Context
) : ViewModel() {
    private var list = Data.getAlarmList()
    private var db: DatabaseHelper = DatabaseHelper(context)

    private var liveDataAlarmList = MutableLiveData<MutableList<Alarm>>()
    private val alarmScheduler = AlarmScheduler(context)

    init {
        liveDataAlarmList = MutableLiveData()
        liveDataAlarmList.value = list
    }

    fun getScheduler() = alarmScheduler
    fun getLiveDataList() = liveDataAlarmList
    fun getDatabase() = db

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadAlarmsData() {
        //load alarms
        list.clear()
        db.getAllAlarms(list)

        list.sortWith(compareBy({ it.getHour() }, { it.getMinute() }))

        liveDataAlarmList.value = list
    }

    fun addToList(alarm: Alarm) {
        list.add(alarm)
        db.addAlarm(alarm)

        //sort the list after adding new alarm
        list.sortWith(compareBy({ it.getHour() }, { it.getMinute() }))

        //update the live data
        liveDataAlarmList.value = list
    }

    fun removeFromList(position: Int) {
        alarmScheduler.cancel(list[position])

        db.removeAlarm(list[position])
        list.removeAt(position)

        liveDataAlarmList.value = list
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editList(newAlarm: Alarm, position: Int) {
        //edit the alarm, cancel the old one if on and schedule the new one
        if (list[position].getStatus()) alarmScheduler.cancel(list[position])

        db.editAlarm(list[position], newAlarm)

        list[position] = newAlarm
        list[position].setStatus(true)

        alarmScheduler.schedule(list[position])

        //update the livedata
        list.sortWith(compareBy({ it.getHour() }, { it.getMinute() }))
        liveDataAlarmList.value = list
    }

    fun clearAlarms() {
        list.clear()
        db.clearAllAlarms()

        liveDataAlarmList.value = list
    }

    fun turnOff(alarm: Alarm) {
        //this alarm is already check if repeat or not -> kill without checking
        for (refAlarm in list)
            if (refAlarm.getHour() == alarm.getHour() && refAlarm.getMinute() == alarm.getMinute())
                refAlarm.setStatus(false)

        //update the livedata
        liveDataAlarmList.value = list
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.setSound(null, null)
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}