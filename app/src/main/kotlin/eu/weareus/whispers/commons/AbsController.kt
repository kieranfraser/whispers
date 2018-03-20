package eu.weareus.whispers.commons

import com.esafirm.conductorextra.butterknife.BinderController
import eu.weareus.whispers.di.components.ActivityComponent
import eu.weareus.whispers.di.components.ControllerComponent
import eu.weareus.whispers.di.helpers.HasComponent
import eu.weareus.whispers.di.modules.ControllerModule

abstract class AbsController : BinderController() {

    @Suppress("UNCHECKED_CAST")
    protected val component: ControllerComponent by lazy {
        if (activity == null) {
            throw IllegalStateException("Not attached to Activity")
        }
        if ((activity is HasComponent<*>).not()) {
            throw IllegalStateException("Activity should implement HasComponent<Component>")
        }
        activity.let { it as HasComponent<ActivityComponent> }
                .getComponent()
                .controllerComponent()
                .controllerModule(ControllerModule(this))
                .build()
    }
}
