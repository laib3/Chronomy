package it.polito.mainactivity.model

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.MutableLiveData
import it.polito.mainactivity.R
import org.json.JSONObject
import java.util.*

class TimeslotModel(val application: Application) {

    private val SHARED_PREFERENCES_NAME: String = application.resources.getString(R.string.shared_preferences)
    private val TIMESLOTS_TAG: String = application.resources.getString(R.string.timeslots_tag)
    private var timeslots: List<Timeslot> = listOf()
    private val sharedPreferences = application.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    init {
        loadTimeslots()
    }

    override fun toString() = """{"timeslots": $timeslots}"""

    private fun loadTimeslots() {
        val timeslotsString = sharedPreferences?.getString(TIMESLOTS_TAG, null)
        val jsonTimeslots: JSONObject? = when(timeslotsString) {
            null -> null
            else -> JSONObject(timeslotsString)
        }
        timeslots = when(jsonTimeslots){
            null -> createTimeslots()
            else -> {
                val jsonArray = jsonTimeslots.getJSONArray("timeslots")
                val jsonTimeslots = Utils.JSONArrayToList(jsonArray)
                jsonTimeslots.map{jt -> Utils.JSONObjectToTimeslot(jt)}
            }
        }
    }

    private fun createTimeslots(): List<Timeslot> {
        return listOf(
            Timeslot("Bring grocery shopping to your door",
                "I'll be happy to receive a list of goods to buy for you and to bring it back home to you. I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to, but please don't choose those outside of the neighbourhood.",
                GregorianCalendar(2022, 4, 25),
                "09:10 AM", "10:00 PM",
                "New Neighbourhood, Street 10, Sydney",
                "Delivery",
                null,
                listOf(),
                null),

            Timeslot("Walk your dog",
                "Donec eu dui nec nisl egestas tristique suscipit non mauris. Nullam nec magna neque. Quisque a quam sodales quam dapibus euismod non et diam. Nulla molestie ex orci, vitae suscipit velit viverra non. Phasellus diam massa, sollicitudin ac interdum commodo.",
                GregorianCalendar(2022, 4, 25),
                "09:10 AM", "10:00 PM",
                "New City, Street 10, Anastasia",
                "Other",
                null,
                listOf(),
                null),

            Timeslot("Teach to your kid",
                "Praesent euismod est ac dictum gravida. Praesent nulla metus, ultrices eu tempor ac, pretium viverra nisi. Morbi odio urna, ornare sit amet dictum in, commodo vel mauris. Vivamus et massa quis lorem iaculis laoreet. Vestibulum eros diam, condimentum ac libero eget, lobortis fringilla orci.",
                GregorianCalendar(2022, 4, 25),
                "09:10 AM", "10:00 PM",
                "New Neighbourhood, Street 10, Sydney",
                "Tutoring",
                null,
                listOf(),
                null),

            Timeslot("Bring grocery YOGA to your door",
                "Phasellus fermentum sagittis leo finibus fringilla. Proin est magna, varius ut arcu lobortis, imperdiet facilisis ex. Mauris varius at metus nec faucibus. Fusce et dapibus ipsum. In hac habitasse platea dictumst. Duis arcu nulla, imperdiet quis placerat eget.",
                GregorianCalendar(2022, 5, 30),
                "09:10 AM", "10:00 PM",
                "New Neighbourhood, Street 10, Sydney",
                "Wellness",
                "monthly",
                listOf(),
                GregorianCalendar(2022, 6, 30)
            ),

            Timeslot("Bring SMTH to your door",
                "Curabitur eu accumsan massa, sed molestie magna. Sed pharetra arcu in leo vulputate feugiat. Nulla finibus dolor non maximus efficitur. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; In nibh mi, posuere vel mi id, pretium consectetur velit.",
                GregorianCalendar(2022, 5, 1),
                "09:10 AM", "10:00 PM",
                "via vincenzo vela, 49, Torino, To, 10128",
                "Wellness",
                "weekly",
                listOf(1, 2, 3),
                GregorianCalendar(2022, 6, 1)
            )
        )
    }

    // save on shared memory
    fun setTimeslots(ts : List<Timeslot>){
        timeslots = ts
        sharedPreferences.edit().putString(TIMESLOTS_TAG, this.toString()).apply()
    }
    fun getTimeslots(): MutableLiveData<List<Timeslot>> = MutableLiveData(timeslots)

}