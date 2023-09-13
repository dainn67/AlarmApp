package com.example.alarmapp.viewmodel

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.alarmapp.models.Alarm
import com.example.alarmapp.models.Constants.Companion.DB_NAME
import com.example.alarmapp.models.Constants.Companion.DB_VERSION
import com.example.alarmapp.models.Constants.Companion.KEY_CONTENT
import com.example.alarmapp.models.Constants.Companion.KEY_HOUR
import com.example.alarmapp.models.Constants.Companion.KEY_ID
import com.example.alarmapp.models.Constants.Companion.KEY_MINUTE
import com.example.alarmapp.models.Constants.Companion.KEY_REPEAT
import com.example.alarmapp.models.Constants.Companion.KEY_STATUS
import com.example.alarmapp.models.Constants.Companion.TABLE_NAME
import com.example.alarmapp.models.Constants.Companion.TAG

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    init {
        val createTableQuery =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME(" +
                    "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$KEY_HOUR INTEGER," +
                    "$KEY_MINUTE INTEGER," +
                    "$KEY_CONTENT TEXT," +
                    "$KEY_REPEAT INTEGER," +
                    "$KEY_STATUS INTEGER)"
        this.writableDatabase.execSQL(createTableQuery)
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME(" +
                    "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$KEY_HOUR INTEGER," +
                    "$KEY_MINUTE INTEGER," +
                    "$KEY_CONTENT TEXT," +
                    "$KEY_REPEAT INTEGER," +
                    "$KEY_STATUS INTEGER)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $DB_NAME")
        onCreate(db)
    }

    fun addAlarm(alarm: Alarm): Long {
        val db = this.writableDatabase
        val newRow = ContentValues().apply {
            put(KEY_HOUR, alarm.getHour())
            put(KEY_MINUTE, alarm.getMinute())
            put(KEY_CONTENT, alarm.getContent())
            put(KEY_REPEAT, if (alarm.getRepeat()) 1 else 0)
            put(KEY_STATUS, if (alarm.getStatus()) 1 else 0)
        }

        val id = db.insert(TABLE_NAME, null, newRow)
        db.close()

        return id
    }

    @SuppressLint("Range")
    fun getAllAlarms(list: MutableList<Alarm>) {
        val db = this.readableDatabase
        val selectAllQuery = "SELECT * FROM $TABLE_NAME"

        val cursor: Cursor? = db.rawQuery(selectAllQuery, null)

        if (cursor != null)
            if (cursor.moveToFirst())        //if there's at least 1 row in the result
                do {
                    val hour = cursor.getInt(cursor.getColumnIndex(KEY_HOUR))
                    val minute = cursor.getInt(cursor.getColumnIndex(KEY_MINUTE))
                    val content = cursor.getString(cursor.getColumnIndex(KEY_CONTENT))
                    val repeat = cursor.getInt(cursor.getColumnIndex(KEY_REPEAT))
                    val status = cursor.getInt(cursor.getColumnIndex(KEY_STATUS))

                    val alarm = Alarm(hour, minute, content, repeat == 1, status == 1)
                    list.add(alarm)
                    Log.i(TAG, "found -> ${list.size}")
                } while (cursor.moveToNext())

        cursor?.close()
        db.close()
    }

    fun clearAllAlarms() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }

    fun editAlarm(oldAlarm: Alarm, newAlarm: Alarm) {
        val db = this.writableDatabase
        val updateQuery =
            "UPDATE $TABLE_NAME " +
                    "SET $KEY_HOUR = ${newAlarm.getHour()}, " +
                    "$KEY_MINUTE = ${newAlarm.getMinute()}, " +
                    "$KEY_CONTENT = ${newAlarm.getContent().ifEmpty { "\"\"" }}, " +
                    "$KEY_REPEAT = ${if (newAlarm.getRepeat()) 1 else 0}, " +
                    "$KEY_STATUS = 1 " +
                    "WHERE $KEY_HOUR = ${oldAlarm.getHour()} AND $KEY_MINUTE = ${oldAlarm.getMinute()}"
        db.execSQL(updateQuery)
        db.close()
    }

    fun editToggleSwitch(currentAlarm: Alarm, status: Boolean) {
        val db = this.writableDatabase
        val updateQuery =
            "UPDATE $TABLE_NAME " +
                    "SET $KEY_STATUS = $status " +
                    "WHERE $KEY_HOUR = ${currentAlarm.getHour()} AND $KEY_MINUTE = ${currentAlarm.getMinute()}"
        db.execSQL(updateQuery)
        db.close()
    }

    fun removeAlarm(alarm: Alarm) {
        val db = this.writableDatabase
        db.delete(
            TABLE_NAME,
            "$KEY_HOUR = ${alarm.getHour()} AND $KEY_MINUTE = ${alarm.getMinute()}",
            null
        )

        db.close()
    }
}