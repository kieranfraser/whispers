package eu.weareus.whispers.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import eu.weareus.whispers.navigator.AppNavigator
import eu.weareus.whispers.navigator.IntentNavigator

@Module
class NavigatorModule(private val navigator: AppNavigator) {

    @Provides
    fun provideAppNavigator(): AppNavigator = navigator

    @Provides
    fun provideIntentNavigator(context: Context): IntentNavigator = IntentNavigator(context)
}
