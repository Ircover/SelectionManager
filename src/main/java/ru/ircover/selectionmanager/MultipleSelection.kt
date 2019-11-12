package ru.ircover.selectionmanager

class MultipleSelection : SelectionManager {
    private val listeners: ArrayList<(position: Int, isSelected: Boolean) -> Unit> = arrayListOf()
    private val selectedPositions: MutableSet<Int> = mutableSetOf()
    private val interceptors: ArrayList<(Int, Boolean, () -> Unit) -> Unit> = arrayListOf()
    override fun clearSelection() {
        selectedPositions.sortedBy { it }
                .forEach { position -> notifyListeners(position, false) }
        selectedPositions.clear()
    }

    override fun selectPosition(position: Int) {
        if(isPositionSelected(position)) {
            interceptors.withInterception(position, false) {
                notifyListeners(position, false)
                selectedPositions.remove(position)
            }
        } else {
            interceptors.withInterception(position, true) {
                notifyListeners(position, true)
                selectedPositions.add(position)
            }
        }
    }

    override fun isPositionSelected(position: Int) = selectedPositions.contains(position)

    override fun registerSelectionChangeListener(listener: (position: Int, isSelected: Boolean) -> Unit) =
            createDisposableForListenerRegistration(listeners, listener)

    override fun getSelectedPositions(): ArrayList<Int> = selectedPositions.sortedBy { it }
            .toCollection(arrayListOf())

    override fun isAnySelected() = selectedPositions.isNotEmpty()

    override fun addSelectionInterceptor(interceptor: (Int, Boolean, () -> Unit) -> Unit) =
            createDisposableForListenerRegistration(interceptors, interceptor)

    private fun notifyListeners(position: Int, isSelected: Boolean) {
        listeners.forEach { it(position, isSelected) }
    }

}