package eu.weareus.whispers.di.helpers

interface HasComponent<T> {
    fun getComponent(): T
}
