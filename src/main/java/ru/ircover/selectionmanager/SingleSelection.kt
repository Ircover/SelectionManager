package ru.ircover.selectionmanager

class SingleSelection : SelectionManager {
    private var selectedPosition = POSITION_INVALID
    private val listeners: ArrayList<(position: Int, isSelected: Boolean) -> Unit> = arrayListOf()
    private val interceptors: ArrayList<(Int, Boolean, () -> Unit) -> Unit> = arrayListOf()

    override fun clearSelection() {
        if(selectedPosition != POSITION_INVALID) {
            notifyListeners(selectedPosition, false)
        }
        selectedPosition = POSITION_INVALID
    }
    override fun selectPosition(position: Int) {
        if(selectedPosition != position) {
            interceptors.launch(position, true) {
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

    override fun <T> getSelectedItems(itemsMapper: (Int) -> T): ArrayList<T> {
        val result = arrayListOf<T>()
        if(selectedPosition != POSITION_INVALID) {
            result.add(itemsMapper(selectedPosition))
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