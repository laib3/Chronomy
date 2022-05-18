package it.polito.mainactivity.ui.timeslot

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.mainactivity.R
import it.polito.mainactivity.data.Timeslot
import it.polito.mainactivity.model.Utils
import java.util.*

class TimeslotViewModel(application: Application) : AndroidViewModel(application) {

    // instantiate timeslot model - it will be a repository in future (?)
    // /private val model: TimeslotModel = TimeslotModel(application)

    private val TIME_LENGTH: Int = 5

    // /private val _timeslots: MutableLiveData<List<Timeslot>> = model.getTimeslots()
    // /val timeslots : LiveData<List<Timeslot>> = _timeslots
    private val _timeslots= MutableLiveData<List<Timeslot>>()
    val timeslots: LiveData<List<Timeslot>> = _timeslots

    private val _submitTimeslot: MutableLiveData<Timeslot> = MutableLiveData<Timeslot>().apply{ value = Timeslot.emptyTimeslot() }
    val submitTimeslot: LiveData<Timeslot> = _submitTimeslot

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var lTimeslots: ListenerRegistration

    init {
        // db = FirebaseFirestore.getInstance()
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
        //lUsers.remove()
        lTimeslots.remove()
    }
    fun DocumentSnapshot.toTimeslot(): Timeslot? {
        return try {
            val title = get("title") as String
            val description = get("description") as String
            // TODO: ADD OTHER FIELDS FROM DB
            val startDate = GregorianCalendar(2022, 5, 25)
            val startHour = get("startHour") as String
            val endHour = get("endHour") as String
            val location = get("location") as String
            val category = get("category") as String
            val repetition = null
            val days = listOf(GregorianCalendar(2022, 5, 25).get(Calendar.DAY_OF_WEEK))
            val endRepetitionDate = GregorianCalendar(2022, 5, 25)

            Timeslot(id, title, description, startDate, startHour, endHour,location, category, repetition, days, endRepetitionDate)



        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }




    // TODO: CHECK IT!
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
                // TODO: Substitute with real values
                //"repetition" to null,
                //"days" to null,
                //"startDate" to null,
                //"endRepetitionDate" to null
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