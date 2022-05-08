package it.polito.mainactivity.ui.timeslot

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.TimeslotModel
import java.util.*

class TimeslotViewModel(application: Application) : AndroidViewModel(application) {

    // instantiate timeslot model - it will be a repository in future (?)
    private val model: TimeslotModel = TimeslotModel(application)

    private val _timeslots: MutableLiveData<List<Timeslot>> = model.getTimeslots()
    val timeslots : LiveData<List<Timeslot>> = _timeslots

    fun findById(id: Int) : Timeslot? {
        return _timeslots.value?.elementAtOrNull(id)
    }

    fun setTimeslots(ts: List<Timeslot>?) = ts?.let{ _timeslots.value = it; model.setTimeslots(it) }

    fun addTimeslot(t : Timeslot?){
        if(t != null){
            val ts = _timeslots.value?.toMutableList().also{ it?.add(t) }
            _timeslots.value = ts!!
            model.setTimeslots(ts)
        }
    }

    fun removeTimeslot(position: Int) {
        val ts = _timeslots.value?.toMutableList().also { it?.removeAt(position) }
        _timeslots.value = ts!!
        model.setTimeslots((ts))
    }

}