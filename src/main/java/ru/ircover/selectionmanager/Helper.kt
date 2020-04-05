package ru.ircover.selectionmanager

internal fun <TListener> createDisposableForListenerRegistration(listenersArrayList: ArrayList<TListener>, listener: TListener): Disposable {
    listenersArrayList.add(listener)
    return object : Disposable {
        override fun dispose() {
            listenersArrayList.remove(listener)
        }
    }
}