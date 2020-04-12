package ru.ircover.selectionmanager

open class SelectableDataSource<T>(dataSource: ArrayList<T>,
                                   private val selectionManager: SelectionManager)
    : SelectionManager by selectionManager {
    var dataSource: ArrayList<T> = dataSource
        private set
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
                this.dataSource = dataSource.cloneTypedly()
                selectionManager.clearSelection()
            }
            ChangeDataSourceMode.HoldSelectedPositions -> {
                this.dataSource = dataSource.cloneTypedly()
                selectionManager.getSelectedPositions()
                        .filter { it >= dataSource.size }
                        .forEach(::deselectPosition)
            }
            ChangeDataSourceMode.HoldSelectedItems -> {
                val lastItems = this.dataSource
                this.dataSource = dataSource.cloneTypedly()
                val lastSelectedPosition = selectionManager.getSelectedPositions()
                lastSelectedPosition.filterWrongPositionItems(lastItems, dataSource)
                        .forEach(::deselectPosition)
                lastSelectedPosition.asSequence()
                        .filter { it < dataSource.size }
                        .map { dataSource.indexOf(lastItems[it]) }
                        .filter { it != POSITION_INVALID && !selectionManager.isPositionSelected(it) }
                        .forEach(::clickPosition)
            }
        }
    }

    override fun registerSelectionChangeListener(listener: (position: Int, isSelected: Boolean) -> Unit): Disposable =
        selectionManager.registerSelectionChangeListener { position, isSelected ->
            if(position >= 0 && position < dataSource.size){
                listener(position, isSelected)
            }
        }

    fun registerItemSelectionChangeListener(listener: (item: T, isSelected: Boolean) -> Unit): Disposable =
        registerSelectionChangeListener { position, isSelected ->
            listener(dataSource[position], isSelected)
        }

    private fun ArrayList<Int>.filterWrongPositionItems(arrayListSource: ArrayList<T>, arrayListDest: ArrayList<T>) =
        filter { !arrayListDest.contains(arrayListSource[it]) || arrayListDest.indexOf(arrayListSource[it]) != it }

    @Suppress("UNCHECKED_CAST")
    private fun <T> ArrayList<T>.cloneTypedly() = clone() as ArrayList<T>
}