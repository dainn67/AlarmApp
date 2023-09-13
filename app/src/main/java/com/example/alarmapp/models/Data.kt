package com.example.alarmapp.models

class Data {
    companion object {
        private var alarmList = mutableListOf<Alarm>()

        fun getAlarmList() = alarmList
    }
}