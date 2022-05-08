package it.polito.mainactivity

import android.app.Application
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.UserProfileModel
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun testTimeslotToString(){
        val t : Timeslot =
        Timeslot("Bring SMTH to your door",
            "description",
            GregorianCalendar(2022, 5, 1),
            "09:10 AM", "10:00 PM",
            "via vincenzo vela, 49, Torino, To, 10128",
            "Wellness",
            "weekly",
            listOf(1, 2, 3),
            GregorianCalendar(2022, 6, 1)
        )
        val s: String = t.toString()
        assertNull(s)
    }
}