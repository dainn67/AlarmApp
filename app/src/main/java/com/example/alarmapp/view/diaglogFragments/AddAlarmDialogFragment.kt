package com.example.alarmapp.view.diaglogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.alarmapp.R
import com.example.alarmapp.models.Alarm
import com.example.alarmapp.viewmodel.MyViewModel

class AddAlarmDialogFragment(private val myViewModel: MyViewModel) : DialogFragment() {
    private lateinit var timePicker: TimePicker
    private lateinit var btnCancel: Button
    private lateinit var btnAdd: Button
    private lateinit var etContent: EditText
    private lateinit var checkboxRepeat: CheckBox

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val myInflater = requireActivity().layoutInflater
        val view = myInflater.inflate(R.layout.fragment_add_alarm_dialog, null, false)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

        //view binding
        timePicker = view.findViewById(R.id.timePicker)
        etContent = view.findViewById(R.id.etContent)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnAdd = view.findViewById(R.id.btnAdd)
        checkboxRepeat = view.findViewById(R.id.checkboxRepeat)

        timePicker.setIs24HourView(true)

        btnCancel.setOnClickListener {
            this.dismiss()
        }
        btnAdd.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val content = etContent.text.toString()
            val isRepeated = checkboxRepeat.isChecked

            val alarm = Alarm(hour, minute, content, isRepeated, true)
            myViewModel.addToList(alarm)

            myViewModel.getScheduler().schedule(alarm)
            this.dismiss()
        }

        return builder.create()
    }
}