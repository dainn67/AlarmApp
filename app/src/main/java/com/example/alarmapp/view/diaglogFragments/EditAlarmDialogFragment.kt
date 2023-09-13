package com.example.alarmapp.view.diaglogFragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.alarmapp.R
import com.example.alarmapp.models.Alarm
import com.example.alarmapp.models.Constants.Companion.TAG
import com.example.alarmapp.viewmodel.MyViewModel

class EditAlarmDialogFragment(
    private val myAlarm: Alarm,
    private val position: Int,
    private val myViewModel: MyViewModel
) : DialogFragment() {
    private lateinit var timePicker: TimePicker
    private lateinit var etContent: EditText
    private lateinit var checkboxRepeat: CheckBox
    private lateinit var btnCancel: Button
    private lateinit var btnChange: Button

    private var isTimeChanged = false
    private var isContentChanged = false
    private var isRepeatChanged = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //debug
        Log.i(TAG, "${myAlarm.getHour()}:${myAlarm.getMinute()} - ${myAlarm.getContent()} - ${myAlarm.getRepeat()}")

        val myInflater = requireActivity().layoutInflater
        val view = myInflater.inflate(R.layout.fragment_add_alarm_dialog, null, false)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

        //view binding
        timePicker = view.findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)
        etContent = view.findViewById(R.id.etContent)
        checkboxRepeat = view.findViewById(R.id.checkboxRepeat)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnChange = view.findViewById(R.id.btnAdd)
        btnChange.text = "Change"
        btnChange.isEnabled = false

        //set timePicker to item's time
        timePicker.currentHour = myAlarm.getHour()
        timePicker.currentMinute = myAlarm.getMinute()
        etContent.hint = myAlarm.getContent()
        checkboxRepeat.isChecked = myAlarm.getRepeat()

        //if user change any data, they can update the alarm
        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            isTimeChanged = hourOfDay != myAlarm.getHour() || minute != myAlarm.getMinute()
            checkEnableBtn()
        }
        etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isContentChanged = etContent.text.isNotEmpty()
                checkEnableBtn()
            }
        })
        checkboxRepeat.setOnCheckedChangeListener{_, isChecked ->
            isRepeatChanged = isChecked != myAlarm.getRepeat()
            checkEnableBtn()
        }

        //buttons to change or cancel
        btnChange.setOnClickListener {
            //create new alarm
            val newContent = if(etContent.text.isNotEmpty() && etContent.text.toString() != myAlarm.getContent()) etContent.text.toString() else myAlarm.getContent()
            val newAlarm = Alarm(timePicker.hour, timePicker.minute, newContent, checkboxRepeat.isChecked, true)

            Log.i(TAG, myAlarm.toString())
            myViewModel.editList(newAlarm, position)

            this.dismiss()
        }

        btnCancel.setOnClickListener { this.dismiss() }
        return builder.create()
    }

    private fun checkEnableBtn(){
        btnChange.isEnabled = isTimeChanged || isRepeatChanged || isContentChanged
    }
}