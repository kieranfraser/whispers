package eu.weareus.whispers.commons

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.bluelinelabs.conductor.ChangeHandlerFrameLayout
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.internal.LifecycleHandler
import com.esafirm.conductorextra.getTopController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import eu.weareus.whispers.MainApp
import eu.weareus.whispers.R
import eu.weareus.whispers.di.components.ActivityComponent
import eu.weareus.whispers.di.helpers.HasComponent
import eu.weareus.whispers.di.modules.ActivityModule
import eu.weareus.whispers.di.modules.NavigatorModule
import eu.weareus.whispers.navigator.AppNavigator
import eu.weareus.whispers.screens.MainController
import java.util.*




class MainActivity : AppCompatActivity(), HasComponent<ActivityComponent> {

    val TAG = javaClass.simpleName;

    companion object {
        const val LAYOUT_ID_MAIN = 0x1
        const val LAYOUT_ID_OVERLAY = 0x2
    }

    val RC_SIGN_IN = 123
    val providers = Arrays.asList(
            AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
            AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build())


    private lateinit var router: Router
    private lateinit var overlayRouter: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )

        val main = generateLayout(LAYOUT_ID_MAIN, params)
        val overlay = generateLayout(LAYOUT_ID_OVERLAY, params)

        setContentView(FrameLayout(this).apply {
            layoutParams = params
            addView(main)
            addView(overlay)
        })

        val lifecycleHandler = LifecycleHandler.install(this)
        router = lifecycleHandler.getRouter(main, savedInstanceState)
        overlayRouter = lifecycleHandler.getRouter(overlay, savedInstanceState)

        router.rebindIfNeeded()
        overlayRouter.rebindIfNeeded()

        activityComponent.appNavigator().setupContent()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser==null)
            startAuthUI()

    }

    fun startAuthUI(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.mipmap.ic_launcher)
                        .build(),
                RC_SIGN_IN)
    }

    private fun generateLayout(id: Int, params: FrameLayout.LayoutParams) =
            ChangeHandlerFrameLayout(this).apply {
                setId(id)
                layoutParams = params
            }


    override fun onBackPressed() {
        if (!overlayRouter.handleBack() && !router.handleBack()) {
            super.onBackPressed()
        }
    }

    /* --------------------------------------------------- */
    /* > Component */
    /* --------------------------------------------------- */

    override fun getComponent(): ActivityComponent = activityComponent

    private val activityComponent: ActivityComponent by lazy {
        MainApp.appComponent!!
                .activityComponent()
                .activityModule(ActivityModule(this))
                .navigatorModule(NavigatorModule(AppNavigator(router, overlayRouter)))
                .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "Permission Granted")
        if (requestCode === RC_SIGN_IN)
        {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode === RESULT_OK)
            {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().getCurrentUser()
                if(user!=null)
                    (router.getTopController() as MainController).loggedIn()
            }
            else
            {
                // Sign in failed, check response for error code
                // ...
            }
        }
    }

    fun isNotificationListenerGranted(): Boolean {
        val listeners = Settings.Secure.getString(contentResolver,
                "enabled_notification_listeners")
        if (listeners != null) {
            Log.d(TAG, "Listeners are : " + listeners.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            return if (listeners.toString().contains(packageName))
                true
            else
                false
        } else
            return false

    }
}
