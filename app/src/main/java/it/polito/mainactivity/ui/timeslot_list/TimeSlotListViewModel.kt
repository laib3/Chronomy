package it.polito.mainactivity.ui.timeslot_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mainactivity.Timeslot
import java.util.*

class TimeSlotListViewModel : ViewModel() {

    private val _timeslots = MutableLiveData<List<Timeslot>>().apply{
        value = listOf(
            Timeslot("Bring grocery shopping to your door",
            "I'll be happy to receive a list of goods to buy for you and to bring it back home to you."+
                    "I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to,"+
                    " but please don't choose those outside of the neighbourhood.",
            Date(2022, 4, 25),
            "09:10 AM", "10:00 PM",
            "New Neighbourhood, Street 10, Sydney",
            "Delivery") ,

        Timeslot("Walk your dog",
            "I'll be happy to receive a list of dogs from you and to bring it back home to you."+
                    "I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to,"+
                    " but please don't choose those outside of the neighbourhood.",
            Date(2022, 4, 25),
            "09:10 AM", "10:00 PM",
            "New City, Street 10, Anastasia",
            "Other"),

        Timeslot("Teach to your kid",
            "I'll be happy to receive a list of goods to buy for you and to bring it back home to you."+
                    "I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to,"+
                    " but please don't choose those outside of the neighbourhood.",
            Date(2022, 4, 25),
            "09:10 AM", "10:00 PM",
            "New Neighbourhood, Street 10, Sydney",
            "Tutoring"),

            Timeslot("Bring grocery YOGA to your door",
                "I'll be happy to receive a list of goods to buy for you and to bring it back home to you."+
                        "I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to,"+
                        " but please don't choose those outside of the neighbourhood.",
                Date(2022, 4, 25),
                "09:10 AM", "10:00 PM",
                "New Neighbourhood, Street 10, Sydney",
                "Wellness"))

    }

    val timeslots : MutableLiveData<List<Timeslot>> = _timeslots

    fun findById(id: Int) : Timeslot? {
        return timeslots.value?.elementAtOrNull(id)
    }
}