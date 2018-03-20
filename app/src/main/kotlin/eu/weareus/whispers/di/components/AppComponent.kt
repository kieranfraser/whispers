package eu.weareus.whispers.di.components

import android.app.Application
import dagger.Component
import eu.weareus.whispers.di.modules.AppModule
import eu.weareus.whispers.di.modules.ContextServiceModule
import eu.weareus.whispers.di.modules.NetworkModule
import eu.weareus.whispers.network.ApiService
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        ContextServiceModule::class,
        NetworkModule::class
))
interface AppComponent {

    /* --------------------------------------------------- */
    /* > Singletons */
    /* --------------------------------------------------- */

    fun apiService(): ApiService

    /* --------------------------------------------------- */
    /* > Subcomponent */
    /* --------------------------------------------------- */

    fun activityComponent(): ActivityComponent.Builder

    companion object {
        fun initialize(app: Application): AppComponent =
                DaggerAppComponent.builder()
                        .appModule(AppModule((app)))
                        .build()
    }
}
