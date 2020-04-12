package ru.ircover.selectionmanager

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import kotlin.test.assertEquals

class SelectableDataSourceTest {
    private lateinit var selectionManagerMock: SelectionManager

    @BeforeEach
    fun setup() {
        selectionManagerMock = mock(SelectionManager::class.java)
    }

    @Test
    fun clickPosition_Negative_Throws() {
        val dataSource = createDataSource(arrayListOf("test"))

        assertThrows<ArrayIndexOutOfBoundsException> { dataSource.clickPosition(-1) }
    }

    @Test
    fun clickPosition_MoreThenCount_Throws() {
        val items = arrayListOf("test")
        val dataSource = createDataSource(items)

        assertThrows<ArrayIndexOutOfBoundsException> { dataSource.clickPosition(items.size) }
    }

    @Test
    fun clickPosition_Correct() {
        val items = arrayListOf("test")
        val dataSource = createDataSource(items)
        val targetPosition = 0

        dataSource.clickPosition(targetPosition)

        verify(selectionManagerMock).clickPosition(targetPosition)
    }

    @Test
    fun getSelectedItems() {
        val items = arrayListOf("test1", "test2")
        val dataSource = createDataSource(items)
        `when`(selectionManagerMock.getSelectedPositions())
                .thenReturn(arrayListOf(0))
        val expectedResult = arrayListOf("test1")

        val result = dataSource.getSelectedItems()

        assertEquals(expectedResult, result)
    }

    @Test
    fun setDataSource_ClearAllSelection() {
        val items = arrayListOf("test1", "test2")
        val dataSource = createDataSource(items)

        dataSource.setDataSource(arrayListOf("test1", "test3"), ChangeDataSourceMode.ClearAllSelection)

        verify(selectionManagerMock).clearSelection()
        verify(selectionManagerMock, never()).clickPosition(anyInt())
    }

    @Test
    fun setDataSource_HoldSelectedPositions() {
        val items = arrayListOf("test1", "test2")
        val dataSource = createDataSource(items)
        `when`(selectionManagerMock.getSelectedPositions())
                .thenReturn(arrayListOf(0, 1))

        dataSource.setDataSource(arrayListOf("test3"), ChangeDataSourceMode.HoldSelectedPositions)

        verify(selectionManagerMock).deselectPosition(1)
        verify(selectionManagerMock, never()).deselectPosition(intThat { it != 1 })
        verify(selectionManagerMock, never()).clickPosition(anyInt())
    }

    @Test
    fun setDataSource_HoldSelectedItems() {
        val items = arrayListOf("test1", "test2", "test3", "test4", "test5")
        val dataSource = createDataSource(items)
        `when`(selectionManagerMock.getSelectedPositions())
                .thenReturn(arrayListOf(0, 2, 3))
        `when`(selectionManagerMock.isPositionSelected(0))
                .thenReturn(true)

        dataSource.setDataSource(arrayListOf("test1", "test3", "test5"), ChangeDataSourceMode.HoldSelectedItems)

        verify(selectionManagerMock).deselectPosition(2)
        verify(selectionManagerMock).deselectPosition(3)
        verify(selectionManagerMock, never()).deselectPosition(intThat { it !in listOf(2, 3) })
        verify(selectionManagerMock).clickPosition(1)
        verify(selectionManagerMock, never()).clickPosition(intThat { it != 1 })
    }

    private fun createDataSource(arrayList: ArrayList<String>): SelectableDataSource<String> {
        return SelectableDataSource(arrayList, selectionManagerMock)
    }
}