package ru.ircover.selectionmanager

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class MultipleSelectionTest {
    private lateinit var selectionManager: MultipleSelection

    @BeforeEach
    fun setup() {
        selectionManager = MultipleSelection()
        assertFalse(selectionManager.isAnySelected(),
                "there should be no selection at the start")
    }

    @Test
    fun isPositionSelected() {
        val targetPosition1 = 1
        val targetPosition2 = 2

        selectionManager.clickPosition(targetPosition1)
        selectionManager.clickPosition(targetPosition2)

        assertTrue(selectionManager.isPositionSelected(targetPosition2),
                "position $targetPosition2 should be selected")
        assertTrue(selectionManager.isPositionSelected(targetPosition1),
                "position $targetPosition1 should not be erased")

        selectionManager.clickPosition(targetPosition2)
        assertFalse(selectionManager.isPositionSelected(targetPosition2),
                "position $targetPosition2 should be erased")
    }

    @Test
    fun deselectPosition_NotSelected() {
        val targetPosition1 = 1
        val targetPosition2 = targetPosition1 + 1
        selectionManager.clickPosition(targetPosition1)
        selectionManager.clickPosition(targetPosition2)

        selectionManager.deselectPosition(targetPosition2 + 1)

        assertEquals(arrayListOf(targetPosition1, targetPosition2), selectionManager.getSelectedPositions())
    }

    @Test
    fun deselectPosition_Selected() {
        val targetPosition1 = 1
        val targetPosition2 = targetPosition1 + 1
        selectionManager.clickPosition(targetPosition1)
        selectionManager.clickPosition(targetPosition2)

        selectionManager.deselectPosition(targetPosition2)

        assertEquals(arrayListOf(targetPosition1), selectionManager.getSelectedPositions())
    }

    @Test
    fun getSelectedPositions() {
        val targetPosition1 = 1
        val targetPosition2 = 2

        selectionManager.clickPosition(targetPosition1)
        selectionManager.clickPosition(targetPosition2)

        assertEquals(arrayListOf(targetPosition1, targetPosition2), selectionManager.getSelectedPositions(),
                "selected positions list is not correct")
    }

    @Test
    fun registerSelectionChangeListener() {
        val targetPosition1 = 1
        val targetPosition2 = 2
        val targetPosition3 = 3
        var isTargetPosition2Processed = false

        selectionManager.clickPosition(targetPosition1)
        val registrationDisposable = selectionManager.registerSelectionChangeListener { position, isSelected ->
            when(position) {
                targetPosition1 -> {
                    fail("position $targetPosition1 should not be erased")
                }
                targetPosition2 -> {
                    isTargetPosition2Processed = true
                    assertTrue(isSelected,
                            "position $targetPosition2 should be selected")
                }
                targetPosition3 -> {
                    fail("position $targetPosition3 should not be listened")
                }
                else -> {
                    fail("position $position was not ever selected")
                }
            }
        }
        selectionManager.clickPosition(targetPosition2)
        registrationDisposable.dispose()
        selectionManager.clickPosition(targetPosition3)

        assertTrue(isTargetPosition2Processed,
                "position $targetPosition2 select was not processed")
    }

    @Test
    fun addSelectionInterceptor() {
        val targetPosition1 = 1
        val targetPosition2 = 2
        val targetPosition3 = 3
        var isInterceptor1Called = false
        var isInterceptor2Called = false

        selectionManager.clickPosition(targetPosition1)
        var interceptorCallback1: (() -> Unit)? = null
        var interceptorCallback2: (() -> Unit)? = null
        val interceptorDisposable1 = selectionManager.addSelectionInterceptor { position, isSelected, callback ->
            when(position) {
                targetPosition2 ->
                    assertTrue(isSelected,
                            "position $position in interceptor1 should be selected")
                else -> fail("position $position in interceptor1 was not ever selected")
            }
            interceptorCallback1 = callback
            isInterceptor1Called = true
        }
        val interceptorDisposable2 = selectionManager.addSelectionInterceptor { position, isSelected, callback ->
            when(position) {
                targetPosition2 ->
                    assertTrue(isSelected,
                            "position $position in interceptor2 should be selected")
                else -> fail("position $position in interceptor2 was not ever selected")
            }
            interceptorCallback2 = callback
            isInterceptor2Called = true
        }

        selectionManager.clickPosition(targetPosition2)
        assertNotNull(interceptorCallback1, "callback of interceptor1 was not set")
        assertNull(interceptorCallback2, "callback of interceptor2 should not be set yet")
        assertTrue(selectionManager.isPositionSelected(targetPosition1),
                "selection from position $targetPosition1 before interceptor1 callback should not be changed")

        interceptorCallback1?.invoke()
        assertNotNull(interceptorCallback2, "callback of interceptor2 was not set")
        assertTrue(selectionManager.isPositionSelected(targetPosition1),
                "selection from position $targetPosition1 before interceptor2 callback should not be changed")

        interceptorCallback2?.invoke()
        assertTrue(selectionManager.isPositionSelected(targetPosition2),
                "selected position should be $targetPosition2 after all callbacks")

        interceptorDisposable1.dispose()
        interceptorDisposable2.dispose()
        selectionManager.clickPosition(targetPosition3)

        assertTrue(isInterceptor1Called, "interceptor1 was not called")
        assertTrue(isInterceptor2Called, "interceptor2 was not called")
    }

    @Test
    fun clearSelectionWithListener() {
        val targetPosition = 2
        var isListenerCalled = false
        selectionManager.clickPosition(targetPosition)
        selectionManager.registerSelectionChangeListener { position, isSelected ->
            assertFalse(isSelected, "selected position should be cancelled")
            assertEquals(targetPosition, position, "wrong position was cancelled")
            assertFalse(selectionManager.isPositionSelected(targetPosition),
                    "cancelled position should not be selected anymore")
            isListenerCalled = true
        }

        selectionManager.clearSelection()
        assertTrue(isListenerCalled, "listener wasn't called")
    }

    @AfterEach
    fun clearSelection() {
        selectionManager.clearSelection()
        assertFalse(selectionManager.isAnySelected(),
                "\"${ selectionManager::clearSelection.name }\" call should always remove all selection")
        assertTrue(selectionManager.getSelectedPositions().isEmpty(),
                "\"${selectionManager::clearSelection.name}\" call should always clear selected positions")
    }
}