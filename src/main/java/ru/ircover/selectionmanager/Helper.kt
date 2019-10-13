package ru.ircover.selectionmanager

internal fun ArrayList<(Int, Boolean, () -> Unit) -> Unit>.launch(position: Int, isSelected: Boolean, callback: () -> Unit) {
    if(isEmpty()) {
        callback()
    } else {
        launch(0, position, isSelected, callback)
    }
}

internal fun ArrayList<(Int, Boolean, () -> Unit) -> Unit>.launch(interceptorIndex: Int, position: Int, isSelected: Boolean, callback: () -> Unit) {
    this[interceptorIndex](position, isSelected) {
        if(interceptorIndex == size - 1) {
            callback()
        } else {
            launch(interceptorIndex + 1, position, isSelected, callback)
        }
    }
}

internal fun <TListener> createDisposableForListenerRegistration(listenersArrayList: ArrayList<TListener>, listener: TListener): Disposable {
    listenersArrayList.add(listener)
    return object : Disposable {
        override fun dispose() {
            listenersArrayList.remove(listener)
        }
    }
}