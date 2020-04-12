package ru.ircover.selectionmanager

class MultipleSelection : BaseInterceptableSelectionManager() {
    private val listeners: ArrayList<(position: Int, isSelected: Boolean) -> Unit> = arrayListOf()
    private val selectedPositions: MutableSet<Int> = mutableSetOf()
    override fun clearSelection() {
        if(selectedPositions.isNotEmpty()) {
            selectedPositions.sortedBy { it }
                    .forEach(this::deselectPosition)
        }
    }

    override fun clickPosition(position: Int) {
        if(isPositionSelected(position)) {
            interceptors.withInterception(position, false) {
                selectedPositions.remove(position)
                notifyListeners(position, false)
            }
        } else {
            interceptors.withInterception(position, true) {
                selectedPositions.add(position)
                notifyListeners(position, true)
            }
        }
    }

    override fun deselectPosition(position: Int) {
        if(isPositionSelected(position)) {
            interceptors.withInterception(position, false) {
                selectedPositions.remove(position)
                notifyListeners(position, false)
            }
        }
    }

    override fun isPositionSelected(position: Int) = selectedPositions.contains(position)

    override fun registerSelectionChangeListener(listener: (position: Int, isSelected: Boolean) -> Unit) =
            createDisposableForListenerRegistration(listeners, listener)

    override fun getSelectedPositions(): ArrayList<Int> = selectedPositions.sortedBy { it }
            .toCollection(arrayListOf())

    override fun isAnySelected() = selectedPositions.isNotEmpty()

    private fun notifyListeners(position: Int, isSelected: Boolean) {
        listeners.forEach { it(position, isSelected) }
    }

}