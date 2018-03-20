package eu.weareus.whispers.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import butterknife.BindView
import butterknife.OnClick
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import eu.weareus.whispers.R
import eu.weareus.whispers.commons.AbsController
import eu.weareus.whispers.commons.MainActivity
import eu.weareus.whispers.navigator.AppNavigator
import javax.inject.Inject
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener




class MainController : AbsController() {

    val TAG = javaClass.simpleName;
    var ref: DatabaseReference = FirebaseDatabase.getInstance().reference

    @Inject lateinit var navigator: AppNavigator

    @BindView(R.id.rl_overlay) lateinit var rlOverlay: RelativeLayout
    @BindView(R.id.sb_main_capture) lateinit var sbCapture: Switch
    @BindView(R.id.wv_main_passenger) lateinit var displayVideo: WebView
    @BindView(R.id.sb_main_capture_google) lateinit var sbCaptureGoogleHome: Switch
    @BindView(R.id.tv_main_passenger) lateinit var tvVerse: TextView
    @BindView(R.id.bt_launch_login) lateinit var btLogin: Button


    var whispers = true
    val whispersVideo = "<html><body style=\"text-align: center;\"><br> <iframe width=\"320\" height=\"315\" src=\"https://www.youtube.com/embed/zl09pl6Q5yk?rel=0&amp;showinfo=0&amp;start=428;end=515\" frameborder=\"0\" allow=\"encrypted-media\" allowfullscreen></iframe></body></html>";
    val scareAwayDarkVideo = "<html><body style=\"text-align: center;\"><br> <iframe width=\"320\" height=\"315\" src=\"https://www.youtube.com/embed/zl09pl6Q5yk?rel=0&amp;showinfo=0&amp;start=598\" frameborder=\"0\" allow=\"encrypted-media\" allowfullscreen></iframe></body></html>"

    override fun getLayoutResId(): Int = R.layout.controller_main

    init {
        registerForActivityResult(121)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 121) run {
            if((activity as MainActivity).isNotificationListenerGranted()){
                permissionGranted()
            }
            else{
                Toast.makeText(applicationContext, "You must grant permission.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onViewBound(bindingResult: View, savedState: Bundle?) {
        component.inject(this)

        if(FirebaseAuth.getInstance().currentUser!=null) {
            loggedIn()
        }
        sbCaptureGoogleHome.setOnCheckedChangeListener{ buttonView, isChecked ->

            Toast.makeText(applicationContext, "Feature coming soon!", Toast.LENGTH_SHORT).show()

        }

        /*tv.setOnClickListener {
            navigator.goToDetail()
        }*/
    }

    fun setUpVideo(frameVideo:String){
        displayVideo.removeAllViews()
        displayVideo.setWebViewClient(object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view:WebView, url:String):Boolean {
                return false
            }
        })
        val webSettings = displayVideo.getSettings()
        webSettings.setJavaScriptEnabled(true)
        displayVideo.loadData(frameVideo, "text/html", "utf-8")
    }

    fun permissionGranted(){
        Log.d(TAG, "Permission Granted")
        ref.child("users").child(FirebaseAuth.getInstance().currentUser?.uid).child("alexaOn").setValue(true)
        Toast.makeText(activity, "Granted.", Toast.LENGTH_LONG).show()
    }

    fun loggedIn(){
        rlOverlay.visibility = View.GONE
        val alexaRef = ref.child("users").child(FirebaseAuth.getInstance().currentUser?.uid).child("alexaOn")
        alexaRef.addListenerForSingleValueEvent(readListener)
        setUpVideo(whispersVideo)
    }

    var readListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            val alexaOn = dataSnapshot.getValue(Boolean::class.java)
            sbCapture.isChecked = alexaOn ?: false
            sbCapture.setOnCheckedChangeListener{ buttonView, isChecked ->
                if(isChecked){
                    val mainActivity = activity as MainActivity
                    if(!mainActivity.isNotificationListenerGranted()){
                        Toast.makeText(applicationContext, "Please enable Notification Listener.", Toast.LENGTH_LONG).show()
                        startActivityForResult(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 121)
                    }
                    else{
                        Toast.makeText(applicationContext, "Listening for notifications.", Toast.LENGTH_LONG).show()
                        ref.child("users").child(FirebaseAuth.getInstance().currentUser?.uid).child("alexaOn").setValue(true)
                    }
                }
                else {
                    Toast.makeText(applicationContext, "Stopped listening.", Toast.LENGTH_LONG).show()
                    ref.child("users").child(FirebaseAuth.getInstance().currentUser?.uid).child("alexaOn").setValue(false)
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            // ...
        }
    }

    @OnClick(R.id.bt_main_logout)
    fun logout(view: View) {
        ref.child("users").child(FirebaseAuth.getInstance().currentUser?.uid).child("alexaOn").setValue(false)
        AuthUI.getInstance()
                .signOut(applicationContext as Context)
                .addOnCompleteListener {
                    Toast.makeText(applicationContext, "Successfully logged out.", Toast.LENGTH_LONG).show()
                    activity?.finish()
                }
    }

    @OnClick(R.id.ib_main_video_next)
    fun nextVideo(view: View) {
        if(whispers) {
            setUpVideo(scareAwayDarkVideo)
            tvVerse.text = "We should love, we should dream; " +
                    "We should stare at the stars and not just the screens"
            whispers = false
        }
        else{
            setUpVideo(whispersVideo)
            tvVerse.text = "You see, all I need's a whisper, in a world that only shouts"
            whispers = true
        }
    }

    @OnClick(R.id.bt_launch_login)
    fun launchLogin(view: View) {
        (activity as MainActivity).startAuthUI()
    }



}
