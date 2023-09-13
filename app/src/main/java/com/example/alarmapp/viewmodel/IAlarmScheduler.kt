package com.example.alarmapp.viewmodel

import com.example.alarmapp.models.Alarm

interface IAlarmScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarm: Alarm)
}