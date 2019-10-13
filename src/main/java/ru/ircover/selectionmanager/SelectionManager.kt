package ru.ircover.selectionmanager

import java.util.*

interface SelectionManager {
    fun clearSelection()
    fun selectPosition(position: Int)
    fun isPositionSelected(position: Int): Boolean
    fun registerSelectionChangeListener(listener: (position: Int, isSelected: Boolean) -> Unit): Disposable
    fun <T> getSelectedItems(itemsMapper: (Int) -> T): ArrayList<T>
    fun isAnySelected(): Boolean
    fun addSelectionInterceptor(interceptor: (position: Int, isSelected: Boolean, callback: () -> Unit) -> Unit): Disposable
}