package org.example.project

import org.example.project.client.ToastMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToastMessageTest {
    @Test
    fun createsToastMessageWithCorrectProperties() {
        val msg = ToastMessage("Hello", isError = false, id = 123L)
        assertEquals("Hello", msg.message)
        assertEquals(false, msg.isError)
        assertEquals(123L, msg.id)
    }

    @Test
    fun toastMessageIdDefaultsToCurrentTime() {
        val msg1 = ToastMessage("Test", isError = true)
        val msg2 = ToastMessage("Test", isError = true)
        assertTrue(msg2.id >= msg1.id)
    }
}