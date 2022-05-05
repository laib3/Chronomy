package it.polito.mainactivity.model

import android.app.Application
import android.content.Context.MODE_PRIVATE
import org.junit.Assert.*

import org.junit.Test

class UserProfileModelTest {

    @Test
    fun testToString() {
        val app = Application()
        val upm = UserProfileModel(app)
    }
}