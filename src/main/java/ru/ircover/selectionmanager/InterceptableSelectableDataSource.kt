package ru.ircover.selectionmanager

class InterceptableSelectableDataSource<T>(dataSource: ArrayList<T>,
                                           private val selectionManager: InterceptableSelectionManager)
    : SelectableDataSource<T>(dataSource, selectionManager), InterceptableSelectionManager {
    constructor(selectionManager: InterceptableSelectionManager) : this(arrayListOf(), selectionManager)

    override fun addSelectionInterceptor(interceptor: (position: Int,
                                                       isSelected: Boolean,
                                                       callback: () -> Unit) -> Unit) =
            selectionManager.addSelectionInterceptor(interceptor)
}