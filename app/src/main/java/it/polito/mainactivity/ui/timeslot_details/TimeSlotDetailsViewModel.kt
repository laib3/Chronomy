package it.polito.mainactivity.ui.timeslot_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import java.time.DayOfWeek
import kotlin.collections.HashMap

class Timeslot (val title:String,
                val description:String,
                val days: List<Date>,
                val startHour : String,
                val startMinute: String,
                val endHour:String,
                val endMinute:String,
                val location:String,
                val category:String ){

}

class TimeSlotDetailsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Timeslot Details"
    }
    val text: LiveData<String> = _text


    private val _timeslot = MutableLiveData<Timeslot>().apply{
        value = Timeslot("Bring grocery shopping to your door",
                        "I'll be happy to receive a list of goods to buy for you and to bring it back home to you."+
                        "I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to,"+
                        " but please don't choose those outside of the neighbourhood.",
                        listOf(Date(2022, 4, 25), Date(2022, 5, 6)),
                        "09", "00", "19", "00",
                        "New Neighbourhood, Street 10, Sydney",
                        "Delivery")
    }

    val timeslot : MutableLiveData<Timeslot> = _timeslot

}