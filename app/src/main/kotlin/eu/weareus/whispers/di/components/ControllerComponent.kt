package eu.weareus.whispers.di.components

import dagger.Subcomponent
import eu.weareus.whispers.di.modules.ControllerModule
import eu.weareus.whispers.screens.ApiExampleController
import eu.weareus.whispers.screens.DetailController
import eu.weareus.whispers.screens.MainController

@Subcomponent(modules = arrayOf(ControllerModule::class))
interface ControllerComponent {

    /* --------------------------------------------------- */
    /* > Injects */
    /* --------------------------------------------------- */

    fun inject(mainController: MainController)
    fun inject(detailController: DetailController)
    fun inject(apiExampleController: ApiExampleController)

    /* --------------------------------------------------- */
    /* > Builders */
    /* --------------------------------------------------- */

    @Subcomponent.Builder
    interface Builder {
        fun controllerModule(controllerModule: ControllerModule): ControllerComponent.Builder
        fun build(): ControllerComponent
    }
}
