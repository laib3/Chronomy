package it.polito.mainactivity.ui.timeslot

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mainactivity.R
import it.polito.mainactivity.data.Timeslot
import it.polito.mainactivity.model.Utils
import java.util.*

class TimeslotViewModel(application: Application) : AndroidViewModel(application) {

    // instantiate timeslot model - it will be a repository in future (?)

    private val TIME_LENGTH: Int = 5

    private val _timeslots= MutableLiveData<List<Timeslot>>()
    val timeslots: LiveData<List<Timeslot>> = _timeslots

    private val _submitTimeslot: MutableLiveData<Timeslot> = MutableLiveData<Timeslot>().apply{ value = Timeslot.emptyTimeslot() }
    val submitTimeslot: LiveData<Timeslot> = _submitTimeslot

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var lTimeslots: ListenerRegistration

    init {
        lTimeslots = db.collection("timeslots")
            .addSnapshotListener { v, e ->
                if (e==null) {
                    _timeslots.value = v!!.mapNotNull { d -> d.toTimeslot() }
                    Log.d("TIMESLOT", _timeslots.value.toString())
                } else _timeslots.value = emptyList()
            }

    }

    override fun onCleared() {
        super.onCleared()
        lTimeslots.remove()
    }
    private fun DocumentSnapshot.toTimeslot(): Timeslot? {
        return try {
            Timeslot(id,
                get("title") as String,
                get("description") as String,
                Utils.formatStringToDate(get("startDate") as String),
                get("startHour") as String,
                get("endHour") as String,
                get("location") as String,
                get("category") as String,
                get("repetition") as String?,
                get("days") as List<Int>,
                Utils.formatStringToDate(get("endRepetitionDate") as String))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }




    // TODO: REMOVE IT, REPLACE IT WITH UPDATE TIMESLOT
    //fun setTimeslots(ts: List<Timeslot>?) = ts?.let{ _timeslots.value = it; model.setTimeslots(it) }
    fun setTimeslots(ts: List<Timeslot>?) = ts?.let{
        _timeslots.value = it;
        // db
        //     .collection("timeslots")
        //     .document()
        //     .set(mapOf("msg" to "Hello"))
        //     .addOnSuccessListener { it -> Log.d("Firebase", "success ${it.toString()}") }
        //     .addOnFailureListener{ Log.d("Firebase", it.message?:"Error")}
    }

    fun updateTimeslot(newTs: Timeslot) {
        // Retrieve id
        // pass the new timeslot

    }

    private fun addTimeslot(t : Timeslot?): Boolean {
        if(t != null && isValid(t)){
            // TODO: CHECK IT
            // t.let{ val ts = _timeslots.value?.toMutableList(); ts?.add(it); _timeslots.value = ts!!; model.setTimeslots(ts) }
            //t.let{ val ts = _timeslots.value?.toMutableList(); ts?.add(it); _timeslots.value = ts!!; }
            val ts = hashMapOf(
                "title" to t.title,
                "description" to t.description,
                "startHour" to t.startHour,
                "endHour" to t.endHour,
                "location" to t.location,
                "category" to t.category,
                "repetition" to t.repetition,
                "days" to t.days,
                "startDate" to Utils.formatDateToString(t.startDate),
                "endRepetitionDate" to Utils.formatDateToString(t.endRepetitionDate)
            );
            db
                 .collection("timeslots")
                 .document()
                 .set(ts)
                 .addOnSuccessListener { Log.d("Firebase", "New timeslot successfully saved ") }
                 .addOnFailureListener{ Log.d("Firebase", "Error: timeslot not saved correctly")}

            return true
        }
        else
            return false
    }

    fun isValid(t: Timeslot): Boolean {
        val app = getApplication<Application>()
        return t.title.isNotBlank() &&
                t.location.isNotBlank() &&
                t.startHour.length == TIME_LENGTH &&
                t.endHour.length == TIME_LENGTH &&
                t.startHour <= t.endHour &&
                t.category in app.resources.getStringArray(R.array.skills_array) &&
                (t.repetition == null || (
                    t.repetition in app.resources.getStringArray(R.array.repetitionMw) &&
                    t.days.isNotEmpty() &&
                    (t.endRepetitionDate.after(t.startDate) || t.endRepetitionDate == t.startDate)))
    }

    fun removeTimeslot(position: Int) {
        val ts = _timeslots.value?.toMutableList().also { it?.removeAt(position) }
        _timeslots.value = ts!!
        // TODO: fix the following method
        // model.setTimeslots((ts))
    }

    fun submitTimeslot(): Boolean {
        if(addTimeslot(_submitTimeslot.value)){
            resetSubmit()
            return true
        }
        return false
    }

    private fun resetSubmit() {
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
        date?.let{ sTs?.startDate = it }
        startHour?.let{ sTs?.startHour = it }
        endHour?.let{ sTs?.endHour = it }
        location?.let{ sTs?.location = it }
        category?.let{ sTs?.category = it }
        // if you pass an empty string then it means that you want it to be null
        repetition?.let{ sTs?.repetition = if(it == "") null else it }
        days?.let{ sTs?.days = it }
        endRepetitionDate?.let{ sTs?.endRepetitionDate = it }
        _submitTimeslot.value = sTs!!
    }

    fun resetSubmitTimeslot() {
        _submitTimeslot.value = Timeslot.emptyTimeslot()
    }
}