package ru.ircover.selectionmanager

class SingleSelection : SelectionManager {
    private var selectedPosition = POSITION_INVALID
    private val listeners: ArrayList<(position: Int, isSelected: Boolean) -> Unit> = arrayListOf()
    private val interceptors: ArrayList<(Int, Boolean, () -> Unit) -> Unit> = arrayListOf()

    override fun clearSelection() {
        val lastSelectedPosition = selectedPosition
        if(lastSelectedPosition != POSITION_INVALID) {
            selectedPosition = POSITION_INVALID
            notifyListeners(lastSelectedPosition, false)
        }
    }
    override fun selectPosition(position: Int) {
        if(selectedPosition != position) {
            interceptors.withInterception(position, true) {
                if (selectedPosition != POSITION_INVALID) {
                    notifyListeners(selectedPosition, false)
                }
                selectedPosition = position
                notifyListeners(selectedPosition, true)
            }
        }
    }
    override fun isPositionSelected(position: Int) = selectedPosition == position

    override fun registerSelectionChangeListener(listener: (position: Int, isSelected: Boolean) -> Unit) =
            createDisposableForListenerRegistration(listeners, listener)

    override fun getSelectedPositions(): ArrayList<Int> {
        val result = arrayListOf<Int>()
        if(selectedPosition != POSITION_INVALID) {
            result.add(selectedPosition)
        }
        return result
    }

    override fun isAnySelected() = selectedPosition != POSITION_INVALID

    override fun addSelectionInterceptor(interceptor: (Int, Boolean, () -> Unit) -> Unit) =
            createDisposableForListenerRegistration(interceptors, interceptor)

    private fun notifyListeners(position: Int, isSelected: Boolean) {
        listeners.forEach { it(position, isSelected) }
    }
}