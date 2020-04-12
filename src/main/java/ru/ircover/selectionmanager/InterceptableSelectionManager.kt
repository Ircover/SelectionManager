package ru.ircover.selectionmanager

interface InterceptableSelectionManager : SelectionManager {
    fun addSelectionInterceptor(interceptor: (position: Int, isSelected: Boolean, callback: () -> Unit) -> Unit): Disposable
}