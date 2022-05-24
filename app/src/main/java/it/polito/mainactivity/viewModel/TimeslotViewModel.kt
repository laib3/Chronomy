package it.polito.mainactivity.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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

    private val _submitTimeslot: MutableLiveData<Timeslot> = MutableLiveData<Timeslot>()

    // .apply { value = Timeslot(_user.value!!) }
    val submitTimeslot: LiveData<Timeslot> = _submitTimeslot

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var lTimeslots: ListenerRegistration

    init {

        val uId = FirebaseAuth.getInstance().currentUser!!.uid

        db.collection("users").document(uId).addSnapshotListener { value, _ ->
            _user.value = Utils.toUser(value)
            _submitTimeslot.value = Timeslot(_user.value!!)
        }

        lTimeslots = db.collection("timeslots")
            .addSnapshotListener { v, e ->
                if (e == null) {
                    _timeslots.value = v!!.mapNotNull { d -> d.toTimeslot() }
                    Log.d("TIMESLOTS", _timeslots.value.toString())
                } // TODO: choose how to handle => else _timeslots.value = emptyList()
            }

    }

    override fun onCleared() {
        super.onCleared()
        lTimeslots.remove()
    }

    private fun DocumentSnapshot.toTimeslot(): Timeslot? {
        return try {
            Timeslot(
                id,
                get("title") as String,
                get("description") as String,
                Utils.formatStringToDate(get("startDate") as String),
                get("startHour") as String,
                get("endHour") as String,
                get("location") as String,
                get("category") as String,
                get("repetition") as String?,
                (get("days") as List<Number>).map { it.toInt() },
                Utils.formatStringToDate(get("endRepetitionDate") as String),
                Utils.anyToUser(get("user"))
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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


    private fun addTimeslot(t: Timeslot?): Boolean {
        //var success = false;

        if (t != null && isValid(t)) {
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
                "endRepetitionDate" to Utils.formatDateToString(t.endRepetitionDate),
                "user" to t.user
            )
            db
                .collection("timeslots")
                .document()
                .set(ts)
                .addOnSuccessListener {
                    Log.d(
                        "TimeslotViewModel",
                        "New timeslot successfully saved "
                    ) //success = true
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

    fun removeTimeslot(id: String?): Boolean {
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

    fun submitTimeslot(): Boolean {
        if (addTimeslot(_submitTimeslot.value)) {
            resetSubmitTimeslot()
            return true
        }
        return false
    }

    fun resetSubmitTimeslot() {
        _submitTimeslot.value = Timeslot(_user.value!!)
    }

    fun setSubmitTimeslotFields(
        title: String? = null,
        description: String? = null,
        date: Calendar? = null,
        startHour: String? = null,
        endHour: String? = null,
        location: String? = null,
        category: String? = null,
        repetition: String? = null,
        days: List<Int>? = null,
        endRepetitionDate: Calendar? = null
    ) {
        val sTs = submitTimeslot.value
        title?.let { sTs?.title = it }
        description?.let { sTs?.description = it }
        date?.let { sTs?.startDate = it }
        startHour?.let { sTs?.startHour = it }
        endHour?.let { sTs?.endHour = it }
        location?.let { sTs?.location = it }
        category?.let { sTs?.category = it }
        // if you pass an empty string then it means that you want it to be null
        repetition?.let { sTs?.repetition = if (it == "") null else it }
        days?.let { sTs?.days = it }
        endRepetitionDate?.let { sTs?.endRepetitionDate = it }
        _submitTimeslot.value = sTs!!
    }

}