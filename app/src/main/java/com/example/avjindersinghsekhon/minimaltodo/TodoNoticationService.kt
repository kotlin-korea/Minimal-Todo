package com.example.avjindersinghsekhon.minimaltodo

import android.app.IntentService
import android.app.Notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import java.util.*

/**
 * Created by kgj11 on 2017-07-23.
 */


class TodoNotificationService : IntentService("TodoNotificationService") {

    override fun onHandleIntent(intent: Intent) {
        val mTodoText = intent.getStringExtra(TODOTEXT)
        val mTodoUUID = intent.getSerializableExtra(TODOUUID) as UUID

        Log.d("OskarSchindler", "onHandleIntent called")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val i = Intent(this, ReminderActivity::class.java)
                .putExtra(TodoNotificationService.TODOUUID, mTodoUUID)
        val deleteIntent = Intent(this, DeleteNotificationService::class.java)
                .putExtra(TODOUUID, mTodoUUID)
        val notification = NotificationCompat.Builder(this)
                .setContentTitle(mTodoText)
                .setSmallIcon(R.drawable.ic_done_white_24dp)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDeleteIntent(PendingIntent.getService(this, mTodoUUID.hashCode(), deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentIntent(PendingIntent.getActivity(this, mTodoUUID.hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT))
                .build()
        manager.notify(100, notification)
    }

    companion object {
        @JvmField
        val TODOTEXT = "com.avjindersekhon.todonotificationservicetext"
        @JvmField
        val TODOUUID = "com.avjindersekhon.todonotificationserviceuuid"
    }
}
