package ru.ircover.selectionmanager

class MultipleSelection : SelectionManager {
    private val listeners: ArrayList<(position: Int, isSelected: Boolean) -> Unit> = arrayListOf()
    private val selectedPositions: MutableSet<Int> = mutableSetOf()
    private val interceptors: ArrayList<(Int, Boolean, () -> Unit) -> Unit> = arrayListOf()
    override fun clearSelection() {
        notifyListenersForSelected(false)
        selectedPositions.clear()
    }

    override fun selectPosition(position: Int) {
        if(isPositionSelected(position)) {
            interceptors.launch(position, false) {
                notifyListeners(position, false)
                selectedPositions.remove(position)
            }
        } else {
            interceptors.launch(position, true) {
                notifyListeners(position, true)
                selectedPositions.add(position)
            }
        }
    }

    override fun isPositionSelected(position: Int) = selectedPositions.contains(position)

    override fun registerSelectionChangeListener(listener: (position: Int, isSelected: Boolean) -> Unit) =
            createDisposableForListenerRegistration(listeners, listener)

    override fun <T> getSelectedItems(itemsMapper: (Int) -> T): ArrayList<T> = selectedPositions.sortedBy { it }
            .mapTo(arrayListOf(), itemsMapper)

    override fun isAnySelected() = selectedPositions.isNotEmpty()

    override fun addSelectionInterceptor(interceptor: (Int, Boolean, () -> Unit) -> Unit) =
            createDisposableForListenerRegistration(interceptors, interceptor)

    private fun notifyListenersForSelected(isSelected: Boolean) {
        selectedPositions.sortedBy { it }
                .forEach { position -> notifyListeners(position, isSelected) }
    }
    private fun notifyListeners(position: Int, isSelected: Boolean) {
        listeners.forEach { it(position, isSelected) }
    }

}