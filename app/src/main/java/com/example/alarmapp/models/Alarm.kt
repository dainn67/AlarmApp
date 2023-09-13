package com.example.alarmapp.models

import java.io.Serializable

class Alarm(
    private var hour: Int,
    private var minute: Int,
    private var content: String,
    private var isRepeat: Boolean,
    private var isOn: Boolean
) : Serializable {
    fun getHour() = hour
    fun getMinute() = minute
    fun getContent() = content
    fun getRepeat() = isRepeat
    fun getStatus() = isOn

    fun setStatus(state: Boolean){
        this.isOn = state
    }

    override fun toString(): String {
        return "$hour:$minute - $content - $isRepeat - $isOn"
    }
}