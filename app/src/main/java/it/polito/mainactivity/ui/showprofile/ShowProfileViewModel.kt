package it.polito.mainactivity.ui.showprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShowProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "ShowProfile"
    }
    val text: LiveData<String> = _text
}