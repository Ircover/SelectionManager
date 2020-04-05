package ru.ircover.selectionmanager

import java.util.*

interface SelectionManager {
    fun clearSelection()
    fun clickPosition(position: Int)
    fun deselectPosition(position: Int)
    fun isPositionSelected(position: Int): Boolean
    fun registerSelectionChangeListener(listener: (position: Int, isSelected: Boolean) -> Unit): Disposable
    fun getSelectedPositions(): ArrayList<Int>
    fun isAnySelected(): Boolean
}