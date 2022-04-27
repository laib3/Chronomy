package it.polito.mainactivity.ui.timeslot_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeSlotEditViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Timeslot Edit"
    }
    val text: LiveData<String> = _text
}