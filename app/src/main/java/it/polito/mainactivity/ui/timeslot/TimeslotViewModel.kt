package it.polito.mainactivity.ui.timeslot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.TimeslotModel
import java.util.*

class TimeslotViewModel(application: Application) : AndroidViewModel(application) {

    // instantiate timeslot model - it will be a repository in future (?)
    private val model: TimeslotModel = TimeslotModel(application)

    private val TIME_LENGTH: Int = 5

    private val _timeslots: MutableLiveData<List<Timeslot>> = model.getTimeslots()
    private val _submitTimeslot: MutableLiveData<Timeslot> = MutableLiveData<Timeslot>().apply{ value = Timeslot.emptyTimeslot() }
    val timeslots : LiveData<List<Timeslot>> = _timeslots
    val submitTimeslot: LiveData<Timeslot> = _submitTimeslot

    fun findById(id: Int) : Timeslot? {
        return _timeslots.value?.elementAtOrNull(id)
    }

    fun setTimeslots(ts: List<Timeslot>?) = ts?.let{ _timeslots.value = it; model.setTimeslots(it) }

    fun addTimeslot(t : Timeslot?): Boolean {
        if(t != null && isValid(t)){
            t?.let{ val ts = _timeslots.value?.toMutableList(); ts?.add(it); _timeslots.value = ts!!; model.setTimeslots(ts) }
            return true
        }
        else
            return false
    }

    fun isValid(t: Timeslot): Boolean{
        val app = getApplication<Application>()
        return t.title.isNotBlank() &&
                t.location.isNotBlank() &&
                t.startHour.length == TIME_LENGTH &&
                t.endHour.length == TIME_LENGTH &&
                t.startHour.compareTo(t.endHour) <= 0 &&
                t.category in app.resources.getStringArray(R.array.skills_array) &&
                (t.repetition == null || (
                    t.repetition in app.resources.getStringArray(R.array.repetitionMw) &&
                    t.days.isNotEmpty() &&
                    (t.endRepetitionDate.after(t.date) || t.endRepetitionDate.equals(t.date))))
    }

    fun removeTimeslot(position: Int) {
        val ts = _timeslots.value?.toMutableList().also { it?.removeAt(position) }
        _timeslots.value = ts!!
        model.setTimeslots((ts))
    }

    fun submitTimeslot(): Boolean {
        if(addTimeslot(_submitTimeslot.value)){
            resetSubmit()
            return true
        }
        return false
    }

    fun resetSubmit() {
        _submitTimeslot.value = Timeslot.emptyTimeslot()
    }

    fun setSubmitFields(title: String? = null,
                        description: String? = null,
                        date: Calendar? = null,
                        startHour : String? = null,
                        endHour:String? = null,
                        location:String? = null,
                        category:String? = null,
                        repetition: String? = null,
                        days: List<Int>? = null,
                        endRepetitionDate: Calendar? = null) {
        val sTs = submitTimeslot.value
        title?.let{ sTs?.title = it }
        description?.let{ sTs?.description = it }
        date?.let{ sTs?.date = it }
        startHour?.let{ sTs?.startHour = it }
        endHour?.let{ sTs?.endHour = it }
        location?.let{ sTs?.location = it }
        category?.let{ sTs?.category = it }
        repetition?.let{ sTs?.repetition = it }
        days?.let{ sTs?.days = it }
        endRepetitionDate?.let{ sTs?.endRepetitionDate = it }
        _submitTimeslot.value = sTs!!
    }

}