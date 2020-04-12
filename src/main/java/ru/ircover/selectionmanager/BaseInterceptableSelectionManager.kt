package ru.ircover.selectionmanager

abstract class BaseInterceptableSelectionManager : InterceptableSelectionManager {
    protected val interceptors: ArrayList<(Int, Boolean, () -> Unit) -> Unit> = arrayListOf()

    override fun addSelectionInterceptor(interceptor: (Int, Boolean, () -> Unit) -> Unit) =
            createDisposableForListenerRegistration(interceptors, interceptor)

    protected fun ArrayList<(Int, Boolean, () -> Unit) -> Unit>.withInterception(position: Int, isSelected: Boolean, callback: () -> Unit) {
        if(isEmpty()) {
            callback()
        } else {
            withInterception(0, position, isSelected, callback)
        }
    }

    private fun ArrayList<(Int, Boolean, () -> Unit) -> Unit>.withInterception(interceptorIndex: Int, position: Int, isSelected: Boolean, callback: () -> Unit) {
        this[interceptorIndex](position, isSelected) {
            if(interceptorIndex == size - 1) {
                callback()
            } else {
                withInterception(interceptorIndex + 1, position, isSelected, callback)
            }
        }
    }
}