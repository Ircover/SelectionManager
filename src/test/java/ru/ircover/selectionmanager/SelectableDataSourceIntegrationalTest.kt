package ru.ircover.selectionmanager

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class SelectableDataSourceIntegrationalTest {
    @Test
    fun holdSelectedPositions_LoweringItems() {
        val dataSource = SelectableDataSource(arrayListOf("test1", "test2", "test3"), MultipleSelection())
        dataSource.clickPosition(0)
        dataSource.clickPosition(2)
        dataSource.registerSelectionChangeListener { _, _ ->
            fail("selection listener shouldn't be called")
        }
        dataSource.setDataSource(arrayListOf("test3"), ChangeDataSourceMode.HoldSelectedPositions)

        assertEquals(arrayListOf("test3"), dataSource.getSelectedItems(), " wrong selected items at the end")
    }
}