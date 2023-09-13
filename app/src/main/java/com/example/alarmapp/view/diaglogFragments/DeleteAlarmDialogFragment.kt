package com.example.alarmapp.view.diaglogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.alarmapp.R
import com.example.alarmapp.models.Alarm
import com.example.alarmapp.viewmodel.MyViewModel

class DeleteAlarmDialogFragment(
    private val myAlarm: Alarm,
    private val myViewModel: MyViewModel,
    private val position: Int
) : DialogFragment() {
    private lateinit var btnCancel: Button
    private lateinit var btnDelete: Button
    private lateinit var tvDeleteTitle: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val myInflater = requireActivity().layoutInflater
        val view = myInflater.inflate(R.layout.delete_alarm_dialog, null, false)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)

        btnCancel = view.findViewById(R.id.btnCancel)
        btnDelete = view.findViewById(R.id.btnDelete)
        tvDeleteTitle = view.findViewById(R.id.deleteTitle)

        val hourFormat: String = if(myAlarm.getHour() < 10) "0${myAlarm.getHour()}" else myAlarm.getHour().toString()
        val minuteFormat: String = if(myAlarm.getMinute() < 10) "0${myAlarm.getMinute()}" else myAlarm.getMinute().toString()
        tvDeleteTitle.text = "Delete alarm at $hourFormat:$minuteFormat ?"

        btnCancel.setOnClickListener { this.dismiss() }
        btnDelete.setOnClickListener {
            myViewModel.removeFromList(position)
            this.dismiss()
        }

        return builder.create()
    }
}