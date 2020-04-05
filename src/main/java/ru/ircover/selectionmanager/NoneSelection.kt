package ru.ircover.selectionmanager

class NoneSelection : BaseInterceptableSelectionManager() {
    override fun clearSelection() { }
    override fun clickPosition(position: Int) { }
    override fun deselectPosition(position: Int) { }
    override fun isPositionSelected(position: Int) = false
    override fun registerSelectionChangeListener(listener: (position: Int, isSelected: Boolean) -> Unit) =
            EmptyDisposable()
    override fun getSelectedPositions() = arrayListOf<Int>()
    override fun isAnySelected() = false
    override fun addSelectionInterceptor(interceptor: (Int, Boolean, () -> Unit) -> Unit) = EmptyDisposable()
}