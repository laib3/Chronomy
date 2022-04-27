package it.polito.mainactivity.ui.timeslot_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeSlotListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Timeslot List"
    }
    val text: LiveData<String> = _text
}