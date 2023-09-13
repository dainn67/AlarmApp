package com.example.alarmapp.view

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.example.alarmapp.R
import com.example.alarmapp.models.Alarm
import com.example.alarmapp.models.Constants
import com.example.alarmapp.view.diaglogFragments.DeleteAlarmDialogFragment
import com.example.alarmapp.view.diaglogFragments.EditAlarmDialogFragment
import com.example.alarmapp.viewmodel.MyViewModel
import com.example.alarmapp.viewmodel.receivers.AlarmReceiver

class AlarmAdapter(
    private val context: Context,
    private val id: Int,
    private val list: List<Alarm>,
    private val myViewModel: MyViewModel,
    private val supportFragmentManager: FragmentManager
) : ArrayAdapter<Alarm>(context, id, list) {
    private lateinit var llItem: LinearLayout
    private lateinit var tvTime: TextView
    private lateinit var tvContent: TextView
    private lateinit var tvRepeated: TextView
    private lateinit var swOnOff: Switch

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val myInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = myInflater.inflate(id, null, false)

        //bind data
        llItem = view.findViewById(R.id.llItem)
        tvTime = view.findViewById(R.id.tvTime)
        tvContent = view.findViewById(R.id.tvContent)
        tvRepeated = view.findViewById(R.id.tvRepeat)
        swOnOff = view.findViewById(R.id.switchOnOff)

        val myAlarm = list[position]

        val hourFormat: String = if(myAlarm.getHour() < 10) "0${myAlarm.getHour()}" else myAlarm.getHour().toString()
        val minuteFormat: String = if(myAlarm.getMinute() < 10) "0${myAlarm.getMinute()}" else myAlarm.getMinute().toString()
        tvTime.text = "$hourFormat:$minuteFormat"
        tvContent.text = myAlarm.getContent()
        tvRepeated.text = if(myAlarm.getRepeat()) "Everyday" else ""
        swOnOff.isChecked = myAlarm.getStatus()

        llItem.setOnClickListener{
            val dialog = EditAlarmDialogFragment(myAlarm, position, myViewModel)
            dialog.show(supportFragmentManager, "editDialog")
        }

        llItem.setOnLongClickListener {
            val dialog = DeleteAlarmDialogFragment(myAlarm, myViewModel, position)
            dialog.show(supportFragmentManager, "deleteDialog")
            true
        }

        swOnOff.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked) myViewModel.getScheduler().schedule(list[position])
            else myViewModel.getScheduler().cancel(list[position])

            val turnOffIntent = Intent(context, AlarmReceiver::class.java)
            val bundle = Bundle()
            bundle.putInt(Constants.KILL_CODE, Constants.ACTION_KILL)
            bundle.putSerializable(Constants.ALARM_CODE, list[position])
            turnOffIntent.putExtras(bundle)

            context.startService(turnOffIntent)

            myAlarm.setStatus(isChecked)
            myViewModel.getDatabase().editToggleSwitch(myAlarm, isChecked)
        }

        return view
    }
}