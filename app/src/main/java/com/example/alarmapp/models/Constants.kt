package com.example.alarmapp.models

class Constants {
    companion object{
        const val TAG = "aaa"
        const val CHANNEL_ID = "MY_CHANNEL_ID"
        const val ACTION_KILL = 1

        const val NOTI_SERVICE_TO_MAIN = "NOTITOMAIN"

        const val TURN_OFF_SWITCH_ALARM_CODE = "TURNOFF_ALARM"
        const val ALARM_CODE = "ALARM_CODE"
        const val BROADCAST_ALARM_CODE = "ALARM_FROM_BROADCAST"

        const val KILL_CODE = "KILL"
        const val TO_KILL_CODE = "TO_KILL"

        const val DB_NAME = "AlarmList.db"
        const val DB_VERSION = 1
        const val TABLE_NAME = "alarm_list"
        const val KEY_ID = "id"
        const val KEY_HOUR = "hour"
        const val KEY_MINUTE = "minute"
        const val KEY_CONTENT = "content"
        const val KEY_REPEAT = "repeat"
        const val KEY_STATUS = "onStatus"
    }
}