package it.polito.mainactivity.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import it.polito.mainactivity.R
import it.polito.mainactivity.model.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class TimeslotViewModel(application: Application) : AndroidViewModel(application) {

    private val TIME_LENGTH: Int = 5

    private val _timeslots = MutableLiveData<List<Timeslot>>()
    val timeslots: LiveData<List<Timeslot>> = _timeslots

    /* following attributes are meaningful only if a new timeslot is being created */
    private val _submitTimeslot: MutableLiveData<Timeslot> = MutableLiveData<Timeslot>()
    val submitTimeslot: LiveData<Timeslot> = _submitTimeslot
    private val _submitRepetitionType: MutableLiveData<String?> =
        MutableLiveData<String?>().apply { value = null }
    val submitRepetitionType: LiveData<String?> = _submitRepetitionType
    private val _submitDaysOfWeek: MutableLiveData<List<Int>> = MutableLiveData<List<Int>>().apply {
        value = listOf(GregorianCalendar.getInstance().get(Calendar.DAY_OF_WEEK))
    }
    val submitDaysOfWeek: LiveData<List<Int>> = _submitDaysOfWeek
    private val _submitEndRepetitionDate: MutableLiveData<Calendar> =
        MutableLiveData<Calendar>().apply { value = GregorianCalendar.getInstance() }
    val submitEndRepetitionDate: LiveData<Calendar> = _submitEndRepetitionDate

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var timeslotsListenerRegistration: ListenerRegistration
    private var ratingsListenerRegistration: ListenerRegistration
    private var chatsListenerRegistration: ListenerRegistration
    private var messagesListenerRegistration: ListenerRegistration

    init {

        // val uId = FirebaseAuth.getInstance().currentUser!!.uid
        timeslotsListenerRegistration =
            db
                .collection("timeslots")
                .addSnapshotListener { tsQuery, error ->
                    if (error == null) {
                        if (tsQuery != null) {
                            viewModelScope.launch {
                                _timeslots.value = tsQuery.mapNotNull { ts ->

                                    val timeslot = Utils.toTimeslotMap(ts)!!
                                    val publisher = ts.get("publisher") as Map<String, Any>
                                    val chatsQuery = ts.reference.collection("chats").get().await()
                                    val chats = getChats(chatsQuery)
                                    val clients = getClients(chatsQuery)
                                    val messages = chatsQuery.documents.map { c ->
                                        val messagesQuery =
                                            c.reference.collection("messages").get().await()
                                        getMessages(messagesQuery)
                                    }.toMutableList()
                                    val ratingsQuery =
                                        ts.reference.collection("ratings").get().await()
                                    val ratings = getRatings(ratingsQuery)

                                    Timeslot(
                                        timeslot,
                                        publisher,
                                        ratings,
                                        chats,
                                        clients,
                                        messages
                                    )
                                }
                            }
                            Log.d("TimeslotViewModel", "fetching timeslots from db")
                        } else {
                            Log.d("TimeslotViewModel", "error when fetching timeslots from db")

                        }
                    }
                    // TODO choose how to handle empty timeslots
                    else {
                        _timeslots.value = emptyList()
                        Log.d("TimeslotViewModel", "error " + error.message)
                    }
                }
        ratingsListenerRegistration =
            db.collectionGroup("ratings").addSnapshotListener { rQuery, error ->
                if (rQuery == null) throw Exception("E")
                rQuery.forEach { r ->
                    _timeslots.value
                        // first parent is the collection of ratings, second is the timeslot
                        ?.find { t -> t.timeslotId == r.reference.parent.parent?.id }
                        .apply {
                            val newRating = Rating(Utils.toRatingMap(r)!!)
                            val newRatings = this?.ratings?.map { oldR ->
                                if (oldR.by == newRating.by)
                                    oldR.apply {
                                        rating = newRating.rating; comment = newRating.comment
                                    }
                                else
                                    oldR
                            }
                            this?.ratings = newRatings!!.toMutableList()
                        }
                }
            }

        chatsListenerRegistration =
            db.collectionGroup("chats").addSnapshotListener { cQuery, error ->
                if (cQuery == null) throw Exception("E")
                cQuery.forEach { c ->
                    _timeslots.value
                        // first parent is the collection of chats, second is the timeslot
                        ?.find { t -> t.timeslotId == c.reference.parent.parent?.id }
                        .apply {
                            val newChat = Chat(Utils.toChatMap(c)!!)
                            val newChats: List<Chat> =
                                this?.chats?.find { c -> c.chatId == newChat.chatId }?.let {
                                    this.chats.map { oldC ->
                                        if (oldC.chatId == newChat.chatId)
                                            oldC.apply {
                                                assigned = newChat.assigned
                                            }
                                        else
                                            oldC
                                    }
                                } ?: let {
                                    this!!.chats.apply { add(newChat) }
                                }
                            this?.chats = newChats.toMutableList()
                        }
                }
            }

        messagesListenerRegistration =
            db.collectionGroup("messages").addSnapshotListener { mQuery, error ->
                if (mQuery == null) throw Exception("E")
                mQuery.forEach { m ->
                    _timeslots.value
                        // first parent is the collection of messages, second parent is the chat document
                        // third parent is the collection of chats, fourth is the timeslot document
                        ?.find { t -> t.timeslotId == m.reference.parent.parent!!.parent.parent?.id }
                        .apply {
                            // Add message if it is new
                            val newMessage = Message(Utils.toMessageMap(m)!!)
                            this?.chats?.find { c -> c.chatId == m.reference.parent.parent!!.id }?.messages!!.apply {
                                if (this.find { message -> message.messageId == newMessage.messageId } == null)
                                    this.apply { add(newMessage) }
                            }
                        }
                }
            }
    }


    override fun onCleared() {
        super.onCleared()
        timeslotsListenerRegistration.remove()
        ratingsListenerRegistration.remove()
        chatsListenerRegistration.remove()
        messagesListenerRegistration.remove()
    }

    fun updateTimeslotField(timeslotId: String, field: String, newValue: Any?): Boolean {
        // var returnValue = false
        db
            .collection("timeslots")
            .document(timeslotId)
            .update(field, newValue)
            .addOnSuccessListener {
                Log.d(
                    "Firebase",
                    "Timeslot updated successfully"
                ) //returnValue = true
                // TODO update requests
                // update offers of current user
                db
                    .collection("users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                    .collection("offers")
                    .document(timeslotId)
                    .update(field, newValue)
                    .addOnSuccessListener {
                        // update requests
                        db.collectionGroup("requests").whereEqualTo("timeslotId", timeslotId).get()
                            .addOnSuccessListener {
                                it.documents.forEach { d -> d.reference.update(field, newValue) }
                            }
                    }
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
        // _submitTimeslot.value = Timeslot(_user.value!!) // default constructor
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
        repetitionType?.let { _submitRepetitionType.value = if (it != "") it else null }
        daysOfWeek?.let { _submitDaysOfWeek.value = it }
        endRepetitionDate?.let { _submitEndRepetitionDate.value = it }
        _submitTimeslot.value = sTs!!
    }

    // TODO do not return a value to check if submission correct, but set a vm attribute
/* submit current timeslot */
    fun submitTimeslot(): Boolean {
        /* check validity of submit fields */
        if (!checkSubmitValid())
            return false
        val t = _submitTimeslot.value
        val dates: List<Calendar> = Utils.createDates(
            t!!.date,
            submitRepetitionType.value,
            submitEndRepetitionDate.value!!,
            submitDaysOfWeek.value!!
        )
        dates.map { date ->
            val id = db.collection("timeslots").document().id
            hashMapOf(
                "timeslotId" to id,
                "publisher" to t.publisher,
                "title" to t.title,
                "description" to t.description,
                "date" to Utils.formatDateToString(date),
                "startHour" to t.startHour,
                "endHour" to t.endHour,
                "location" to t.location,
                "category" to t.category,
                "status" to t.status,
                "chats" to t.chats,
                "ratings" to t.ratings
            )
            // TODO: check if lists and maps are correctly saved on the db
            // TODO: check how the enum is saved on the db, if badly, replace it with string
        }.forEach { tMap ->
            db
                .collection("timeslots")
                .document(tMap["timeslotId"] as String)
                .set(tMap)
                .addOnSuccessListener {
                    Log.d(
                        "Firebase",
                        "New timeslot successfully saved "
                    ) //success = true
                    db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .collection("offers").document().set(tMap).addOnSuccessListener {
                            Log.d("Firebase", "New offer added")
                        }
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

    fun deleteTimeslot(timeslotId: String?): Boolean {
        //var success: Boolean = false;
        timeslotId?.apply {
            db.collection("timeslots")
                .document(timeslotId)
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

    /**
     * Add chat between the owner of the timeslot with the given id and the current user
     */
    fun addChat(timeslotId: String): Boolean {
        // val newChat = Chat(user.value!!, false, mutableListOf())
        val chats = timeslots.value?.find { t -> t.timeslotId == timeslotId }?.chats
        // chats?.add(newChat)

        return updateTimeslotField(timeslotId, "chats", chats)
    }

    fun setChatAssigned(assigned: Boolean): Boolean {
        // TODO:
        return true
    }

    fun addMessage(text: String): Boolean {
        // TODO:
        return true
    }

    fun updateRating(): Boolean {
        // TODO:
        return true
    }

    fun getChats(chatsQuery: QuerySnapshot): MutableList<Map<String, Any>> {
        return chatsQuery.documents
            .map { c -> Utils.toChatMap(c)!! }
            .toMutableList()
    }

    fun getClients(chatsQuery: QuerySnapshot): MutableList<Map<String, Any>> {
        return chatsQuery.documents
            .map { c -> c.get("client")!! as Map<String, Any> }
            .toMutableList()
    }

    fun getMessages(messagesQuery: QuerySnapshot): MutableList<Map<String, String>> {
        return messagesQuery.documents
            .map { m -> Utils.toMessageMap(m)!! }
            .toMutableList()
    }

    fun getRatings(ratingsQuery: QuerySnapshot): MutableList<Map<String, Any>> {
        return ratingsQuery.documents
            .map { r -> Utils.toRatingMap(r)!! }
            .toMutableList()
    }

    fun addSnapshotForMessages() {
        db.collectionGroup("messages").addSnapshotListener { m, error ->
            if (m == null) throw Exception("E")
            viewModelScope.launch {
                m.forEach { ms ->
                    val chat = ms.reference.parent.get().await()
                    val tId = chat.documents.map { cs -> cs.reference.parent.id }[0]
                    timeslots.value?.find { t -> t.timeslotId == tId }.apply { }
                    ms.id
                }
            }
        }
    }

}