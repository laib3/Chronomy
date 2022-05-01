package it.polito.mainactivity.ui.timeslot_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mainactivity.Timeslot
import java.util.*

class TimeSlotListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Timeslot List"
    }
    val text: LiveData<String> = _text

    private val _timeslots = MutableLiveData<List<Timeslot>>().apply{
        value = listOf(
            Timeslot("Bring grocery shopping to your door",
                "I'll be happy to receive a list of goods to buy for you and to bring it back home to you. I have a car so the quantity is not an issue. You can also select which supermarket you want me to go to, but please don't choose those outside of the neighbourhood.",
                Date(2022, 4, 25),
                "09:10 AM", "10:00 PM",
                "New Neighbourhood, Street 10, Sydney",
                "Delivery") ,

            Timeslot("Walk your dog",
                "Donec eu dui nec nisl egestas tristique suscipit non mauris. Nullam nec magna neque. Quisque a quam sodales quam dapibus euismod non et diam. Nulla molestie ex orci, vitae suscipit velit viverra non. Phasellus diam massa, sollicitudin ac interdum commodo.",
                Date(2022, 4, 25),
                "09:10 AM", "10:00 PM",
                "New City, Street 10, Anastasia",
                "Other"),

            Timeslot("Teach to your kid",
                "Praesent euismod est ac dictum gravida. Praesent nulla metus, ultrices eu tempor ac, pretium viverra nisi. Morbi odio urna, ornare sit amet dictum in, commodo vel mauris. Vivamus et massa quis lorem iaculis laoreet. Vestibulum eros diam, condimentum ac libero eget, lobortis fringilla orci.",
                Date(2022, 4, 25),
                "09:10 AM", "10:00 PM",
                "New Neighbourhood, Street 10, Sydney",
                "Tutoring"),

            Timeslot("Bring grocery YOGA to your door",
                "Phasellus fermentum sagittis leo finibus fringilla. Proin est magna, varius ut arcu lobortis, imperdiet facilisis ex. Mauris varius at metus nec faucibus. Fusce et dapibus ipsum. In hac habitasse platea dictumst. Duis arcu nulla, imperdiet quis placerat eget.",
                Date(2022, 4, 30),
                "09:10 AM", "10:00 PM",
                "New Neighbourhood, Street 10, Sydney",
                "Wellness"),

            Timeslot("Bring SMTH to your door",
                "Curabitur eu accumsan massa, sed molestie magna. Sed pharetra arcu in leo vulputate feugiat. Nulla finibus dolor non maximus efficitur. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; In nibh mi, posuere vel mi id, pretium consectetur velit.",
                Date(2022, 5, 1),
                "09:10 AM", "10:00 PM",
                "via vincenzo vela, 49, Torino, To, 10128",
                "Wellness",
                "weekly",
                listOf(1, 2, 3),
                Date(2022, 4, 25)
            )
        )


    }

    val timeslots : MutableLiveData<List<Timeslot>> = _timeslots

    public fun findById(id: Int) : Timeslot? {
        return timeslots.value?.elementAtOrNull(id)
    }
}