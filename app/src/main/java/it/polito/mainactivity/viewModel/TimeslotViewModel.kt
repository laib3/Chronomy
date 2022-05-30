package it.polito.mainactivity.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import it.polito.mainactivity.R
import it.polito.mainactivity.model.Timeslot
import it.polito.mainactivity.model.User
import it.polito.mainactivity.model.Utils
import java.util.*

class TimeslotViewModel(application: Application) : AndroidViewModel(application) {

    private val TIME_LENGTH: Int = 5

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user
    private val _timeslots = MutableLiveData<List<Timeslot>>()
    val timeslots: LiveData<List<Timeslot>> = _timeslots

    /* following attributes are meaningful only if a new timeslot is being created */
    private val _submitTimeslot: MutableLiveData<Timeslot> = MutableLiveData<Timeslot>()
    val submitTimeslot: LiveData<Timeslot> = _submitTimeslot
    private val _submitRepetitionType: MutableLiveData<String?> = MutableLiveData<String?>().apply{ value = null }
    val submitRepetitionType: LiveData<String?> = _submitRepetitionType
    private val _submitDaysOfWeek: MutableLiveData<List<Int>> = MutableLiveData<List<Int>>().apply{ value = listOf(GregorianCalendar.getInstance().get(Calendar.DAY_OF_WEEK)) }
    val submitDaysOfWeek: LiveData<List<Int>> = _submitDaysOfWeek
    private val _submitEndRepetitionDate: MutableLiveData<Calendar> = MutableLiveData<Calendar>().apply{ value = GregorianCalendar.getInstance() }
    val submitEndRepetitionDate: LiveData<Calendar> = _submitEndRepetitionDate

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var lTimeslots: ListenerRegistration

    init {

        val uId = FirebaseAuth.getInstance().currentUser!!.uid

        db.collection("users").document(uId).addSnapshotListener { value, _ ->
            _user.value = Utils.toUser(value)
            _submitTimeslot.value = Timeslot(_user.value!!)
        }

        lTimeslots =
            db
                .collection("timeslots")
                .addSnapshotListener { v, e ->
                if (e == null) {
                    _timeslots.value = v!!.mapNotNull { d -> Utils.toTimeslot(d) }
                    // Log.d("TimeslotViewModel", v.toString())
                }
                // TODO choose how to handle empty timeslots
                else {
                    _timeslots.value = emptyList()
                    Log.d("TimeslotViewModel", "error " + e.message)
                }
            }

    }

    override fun onCleared() {
        super.onCleared()
        lTimeslots.remove()
    }

    fun updateTimeslotField(id: String, field: String, newValue: Any?): Boolean {
        // var returnValue = false
        db
            .collection("timeslots")
            .document(id)
            .update(field, newValue)
            .addOnSuccessListener {
                Log.d(
                    "Firebase",
                    "Timeslot updated successfully"
                ) //returnValue = true
            }
            .addOnFailureListener {
                Log.d(
                    "Firebase",
                    "Error: timeslot not updated correctly"
                ) //returnValue = false
            }
        return true
    }

    /* set current submitTimeslot to a empty timeslot */
    fun resetSubmitFields() {
        _submitTimeslot.value = Timeslot(_user.value!!) // default constructor
        _submitRepetitionType.value = null
        _submitDaysOfWeek.value = listOf(GregorianCalendar.getInstance().get(Calendar.DAY_OF_WEEK))
        _submitEndRepetitionDate.value = GregorianCalendar.getInstance()
    }

    fun setSubmitFields(
        title: String? = null,
        description: String? = null,
        date: Calendar? = null,
        startHour: String? = null,
        endHour: String? = null,
        location: String? = null,
        category: String? = null,
        repetitionType: String? = null,
        daysOfWeek: List<Int>? = null,
        endRepetitionDate: Calendar? = null
    ) {
        val sTs = _submitTimeslot.value
        title?.let { sTs?.title = it }
        description?.let { sTs?.description = it }
        date?.let { sTs?.date = it }
        startHour?.let { sTs?.startHour = it }
        endHour?.let { sTs?.endHour = it }
        location?.let { sTs?.location = it }
        category?.let { sTs?.category = it }
        // if you pass an empty string then it means that you want it to be null
        repetitionType?.let { _submitRepetitionType.value = if(it != "") it else null }
        daysOfWeek?.let { _submitDaysOfWeek.value = it }
        endRepetitionDate?.let { _submitEndRepetitionDate.value = it }
        _submitTimeslot.value = sTs!!
    }

    // TODO do not return a value to check if submission correct, but set a vm attribute
    /* submit current timeslot */
    fun submitTimeslot(): Boolean {
        /* check validity of submit fields */
        if(!checkSubmitValid())
            return false
        val t = _submitTimeslot.value
        val dates: List<Calendar> = Utils.createDates(t!!.date, submitRepetitionType.value, submitEndRepetitionDate.value!!, submitDaysOfWeek.value!!)
        dates.map{ date ->
            val id = db.collection("timeslots").document().id
            hashMapOf(
                "timeslotId" to id,
                "title" to t.title,
                "description" to t.description,
                "startHour" to t.startHour,
                "endHour" to t.endHour,
                "location" to t.location,
                "category" to t.category,
                "date" to Utils.formatDateToString(date),
                "publisher" to user.value
            )
        }.forEach{ tMap ->
            db
                .collection("timeslots")
                .document(tMap["timeslotId"] as String)
                .set(tMap)
                .addOnSuccessListener {
                    Log.d(
                        "Firebase",
                        "New timeslot successfully saved "
                    ) //success = true
                    // TODO check if it works
                    resetSubmitFields()
                }
                .addOnFailureListener {
                    Log.d(
                        "Firebase",
                        "Error: timeslot not saved correctly"
                    ) //success = false
                }
        }
        return true
    }

    /** check validity of a given timeslot **/
    fun isValid(t: Timeslot?): Boolean {
        val app = getApplication<Application>()
        return t != null &&
                t.title.isNotBlank() &&
                t.location.isNotBlank() &&
                t.startHour.length == TIME_LENGTH &&
                t.endHour.length == TIME_LENGTH &&
                t.startHour <= t.endHour &&
                t.category in app.resources.getStringArray(R.array.skills_array)
    }

    fun checkSubmitValid(): Boolean {
        val app = getApplication<Application>()
        return submitTimeslot.value!!.title.isNotBlank() &&
                submitTimeslot.value!!.location.isNotBlank() &&
                submitTimeslot.value!!.startHour.length == TIME_LENGTH &&
                submitTimeslot.value!!.endHour.length == TIME_LENGTH &&
                submitTimeslot.value!!.startHour <= submitTimeslot.value!!.endHour &&
                submitTimeslot.value!!.category in app.resources.getStringArray(R.array.skills_array) &&
                (submitRepetitionType.value == null || (
                        submitRepetitionType.value in app.resources.getStringArray(R.array.repetitionMw) &&
                                submitDaysOfWeek.value!!.isNotEmpty() &&
                                (!submitEndRepetitionDate.value!!.before(submitTimeslot.value!!.date))) /* end repetition date must not be before start date */
                        )
    }

    fun deleteTimeslot(id: String?): Boolean {
        //var success: Boolean = false;
        id?.apply {
            db.collection("timeslots")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    Log.d(
                        "Firebase",
                        "Timeslot successfully deleted!"
                    ) //success = true;
                }
                .addOnFailureListener {
                    Log.d("Firebase", "Error: deleting timeslot") //success = false;
                }
        }
        return true
    }


}