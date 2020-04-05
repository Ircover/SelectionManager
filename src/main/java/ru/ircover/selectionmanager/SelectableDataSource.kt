package ru.ircover.selectionmanager

import kotlin.collections.ArrayList

class SelectableDataSource<T>(private var dataSource: ArrayList<T>,
                              private val selectionManager: SelectionManager) : SelectionManager by selectionManager {
    constructor(selectionManager: SelectionManager) : this(arrayListOf(), selectionManager)

    override fun clickPosition(position: Int) {
        if(position < 0) {
            throw ArrayIndexOutOfBoundsException("Position selection couldn't be less then 0")
        }
        if(position >= dataSource.size) {
            throw ArrayIndexOutOfBoundsException("Position selection couldn't be after last item of data source")
        }
        selectionManager.clickPosition(position)
    }

    fun getSelectedItems(): ArrayList<T> = selectionManager.getSelectedPositions()
            .mapTo(arrayListOf()) { selectedPosition ->
                dataSource[selectedPosition]
            }

    fun setDataSource(dataSource: ArrayList<T>,
                      changeMode: ChangeDataSourceMode = ChangeDataSourceMode.ClearAllSelection) {
        when(changeMode) {
            ChangeDataSourceMode.ClearAllSelection -> {
                this.dataSource = dataSource
                selectionManager.clearSelection()
            }
            ChangeDataSourceMode.HoldSelectedPositions -> {
                this.dataSource = dataSource
                selectionManager.getSelectedPositions()
                        .filter { it >= dataSource.size }
                        .forEach(selectionManager::deselectPosition)
            }
            ChangeDataSourceMode.HoldSelectedItems -> {
                val lastItems = this.dataSource
                this.dataSource = dataSource
                val lastSelectedPosition = selectionManager.getSelectedPositions()
                lastSelectedPosition.filterWrongPositionItems(lastItems, dataSource)
                        .forEach(selectionManager::deselectPosition)
                lastSelectedPosition.asSequence()
                        .filter { it < dataSource.size }
                        .map { dataSource.indexOf(lastItems[it]) }
                        .filter { it != POSITION_INVALID && !selectionManager.isPositionSelected(it) }
                        .forEach(selectionManager::clickPosition)
            }
        }
    }

    private fun ArrayList<Int>.filterWrongPositionItems(arrayListSource: ArrayList<T>, arrayListDest: ArrayList<T>) =
        filter { it < arrayListDest.size &&
                (!arrayListDest.contains(arrayListSource[it]) || arrayListDest.indexOf(arrayListSource[it]) != it) }
}