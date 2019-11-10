package ru.ircover.selectionmanager

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class NoneSelectionTest {
    private lateinit var selectionManager: NoneSelection

    @BeforeEach
    fun setup() {
        selectionManager = NoneSelection()
    }

    @Test
    fun selectPosition() {
        val targetPosition = 1

        selectionManager.selectPosition(targetPosition)

        assertFalse(selectionManager.isPositionSelected(targetPosition))
    }

    @AfterEach
    fun testFinal() {
        assertFalse(selectionManager.isAnySelected(),
                "${ NoneSelection::class } should always have no selection")
        assertTrue(selectionManager.getSelectedPositions().isEmpty(),
                "${ NoneSelection::class } should always have empty selected positions")
    }
}