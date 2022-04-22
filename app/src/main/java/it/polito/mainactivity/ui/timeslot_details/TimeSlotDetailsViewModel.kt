package it.polito.mainactivity.ui.timeslot_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeSlotDetailsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Timeslot Details"
    }
    val text: LiveData<String> = _text
}