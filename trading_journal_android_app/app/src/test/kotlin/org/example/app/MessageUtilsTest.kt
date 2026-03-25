package org.example.app

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

/**
 * Basic unit test for the Trading Journal app.
 */
class MessageUtilsTest {
    @Test
    fun testGetMessage() {
        assertEquals("Hello      World!", MessageUtils.message())
    }
}
