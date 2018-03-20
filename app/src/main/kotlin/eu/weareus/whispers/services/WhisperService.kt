package eu.weareus.whispers.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.SystemClock
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import eu.weareus.whispers.data.Notification

/**
 * Created by Kieran on 18/03/2018.
 */
class WhisperService : NotificationListenerService() {

    val TAG = javaClass.simpleName;
    var ref: DatabaseReference = FirebaseDatabase.getInstance().reference
    var alexaOn: Boolean = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent) {

        val restartService = Intent(applicationContext,
                this.javaClass)
        restartService.`package` = packageName
        val restartServicePI = PendingIntent.getService(
                applicationContext, 1, restartService,
                PendingIntent.FLAG_ONE_SHOT)
        val alarmService = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 10000, restartServicePI)
    }

    override fun onCreate() {
        super.onCreate()
        if(FirebaseAuth.getInstance().currentUser!=null){
            val alexaOnRef = ref.child("users").child(FirebaseAuth.getInstance().currentUser?.uid).child("alexaOn")
            alexaOnRef.addListenerForSingleValueEvent(readListener)

            val alexaRemovalRef = ref.child("notifications").child(FirebaseAuth.getInstance().currentUser?.uid)
            alexaRemovalRef.addChildEventListener(removalListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    var readListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            Log.d(TAG, dataSnapshot.toString())
            val userValue = dataSnapshot.getValue(Boolean::class.java)
            alexaOn = userValue ?: false
            // ...
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    var removalListener: ChildEventListener = object : ChildEventListener {

        override fun onChildAdded(snapshot: DataSnapshot?, p1: String?) {}

        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}

        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot?) {
            Log.d(TAG, "Removing notification via User Command")
            val removedNoti = snapshot?.getValue(Notification::class.java)
            try{
                cancelNotification(removedNoti?.id)
            } catch(e: Exception){
                Log.d(TAG, "Notification may have been removed manually and not logged.")
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        var currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser!=null && alexaOn){
            Log.i(TAG, "Notification Posted! from "+sbn.packageName)
            val notification = Notification(simplePackageName(applicationContext, sbn.packageName),
                    sbn.notification?.tickerText?.toString(), sbn.postTime.toString(), "", sbn.key)
            ref.child("notifications").child(currentUser.uid).child(sbn.postTime.toString()).setValue(notification)
        }

    }

    override fun onNotificationRemoved(sbn:StatusBarNotification) {
        var currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser!=null && alexaOn){
            Log.i(TAG, "Notification Removed")
            ref.child("notifications").child(currentUser.uid).child(sbn.postTime.toString()).removeValue()
        }
    }

    fun simplePackageName(context: Context, packageName: String): String {
        val pm = context.packageManager
        var ai: ApplicationInfo?
        try {
            ai = pm.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            ai = null
        }

        return (if (ai != null) pm.getApplicationLabel(ai) else "(unknown)") as String
    }
}