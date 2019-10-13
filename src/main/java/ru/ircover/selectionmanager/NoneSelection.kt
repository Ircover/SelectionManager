package ru.ircover.selectionmanager

class NoneSelection : SelectionManager {
    override fun clearSelection() { }
    override fun selectPosition(position: Int) { }
    override fun isPositionSelected(position: Int) = false
    override fun registerSelectionChangeListener(listener: (position: Int, isSelected: Boolean) -> Unit) =
            EmptyDisposable()
    override fun <T> getSelectedItems(itemsMapper: (Int) -> T) = arrayListOf<T>()
    override fun isAnySelected() = false
    override fun addSelectionInterceptor(interceptor: (Int, Boolean, () -> Unit) -> Unit) = EmptyDisposable()
}