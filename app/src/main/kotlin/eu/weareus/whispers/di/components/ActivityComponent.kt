package eu.weareus.whispers.di.components

import dagger.Subcomponent
import eu.weareus.whispers.di.modules.ActivityModule
import eu.weareus.whispers.di.modules.NavigatorModule
import eu.weareus.whispers.di.scopes.ActivityScope
import eu.weareus.whispers.navigator.AppNavigator
import eu.weareus.whispers.navigator.IntentNavigator

@ActivityScope
@Subcomponent(modules = arrayOf(
        ActivityModule::class,
        NavigatorModule::class
))
interface ActivityComponent {

    fun appNavigator(): AppNavigator
    fun intentNavigator(): IntentNavigator

    /* --------------------------------------------------- */
    /* > Subcomponent */
    /* --------------------------------------------------- */

    fun controllerComponent(): ControllerComponent.Builder

    /* --------------------------------------------------- */
    /* > Builder */
    /* --------------------------------------------------- */

    @Subcomponent.Builder
    interface Builder {
        fun activityModule(activityModule: ActivityModule): ActivityComponent.Builder
        fun navigatorModule(navigatorModule: NavigatorModule): ActivityComponent.Builder
        fun build(): ActivityComponent
    }
}
