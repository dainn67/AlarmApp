package com.example.alarmapp.viewmodel.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.alarmapp.models.Constants.Companion.KILL_CODE
import com.example.alarmapp.models.Constants.Companion.TO_KILL_CODE
import com.example.alarmapp.viewmodel.NotificationService

class KillReceiver: BroadcastReceiver() {
    private var kill = 0
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras

        if(bundle != null) kill = bundle.getInt(KILL_CODE, 0)

        val sendBackToNotiServiceIntent = Intent(context, NotificationService::class.java)
        val sendBackBundle = Bundle()
        sendBackBundle.putInt(TO_KILL_CODE, kill)
        sendBackToNotiServiceIntent.putExtras(sendBackBundle)

        context?.startService(sendBackToNotiServiceIntent)
    }
}