package it.polito.mainactivity.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
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
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
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

        chatsListenerRegistration = db.collection("chats").addSnapshotListener { cQuery, error ->
            if (cQuery == null) throw Exception("E")
            cQuery.forEach { c ->
                _timeslots.value
                    // first parent is the collection of chats, second is the timeslot
                    ?.find { t -> t.timeslotId == c.reference.parent.parent?.id }
                    .apply {
                        val newChat = Chat(Utils.toChatMap(c)!!, c.get("client") as Map<String, String>)
                        val newChats = this?.chats?.map { oldC ->
                            if (oldC.client["userId"] == newChat.client["userId"])
                                oldC.apply {
                                    assigned = newChat.assigned
                                }
                            else
                                oldC
                        }
                        this?.chats = newChats!!.toMutableList()
                    }
            }
        }

        messagesListenerRegistration=
            db.collection("messages").addSnapshotListener { mQuery, error ->
                if (mQuery == null) throw Exception("E")
                viewModelScope.launch {
                    mQuery.forEach{ m ->
                        val chat = m.reference.parent.parent?.get()?.await()
                        _timeslots.value
                            // first parent is the collection of messages, second parent is the chat document
                            // third parent is the collection of chats, fourth is the timeslot document
                            ?.find { t -> t.timeslotId == m.reference.parent.parent!!.parent.parent?.id}
                            .apply {
                                val newMessage = Message(Utils.toMessageMap(m)!!)
                                // TODO: val newMessages =
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

    // TODO: check if lists and maps are correctly saved on the db
    // TODO: check how the enum is saved on the db, if badly, replace it with string
    private fun createSubmitTimeslotMap(t: Timeslot, date: Calendar, id: String): Map<String, Any> =
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
        )

    /* submit current timeslot */
    fun submitTimeslot(): Boolean {
        /* check validity of submit fields */
        if (!checkSubmitValid())
            return false
        return try {
            val t = _submitTimeslot.value
            val dates: List<Calendar> = Utils.createDates(
                t!!.date,
                submitRepetitionType.value,
                submitEndRepetitionDate.value!!,
                submitDaysOfWeek.value!!
            )
            viewModelScope.launch {
                // create one timeslot for each date
                dates.map { date ->
                    val timeslotId = db.collection("timeslots").document().id
                    createSubmitTimeslotMap(t, date, timeslotId)
                }.forEach { tMap ->
                    val tsRef = db.collection("timeslots").document(tMap["timeslotId"] as String)
                    tsRef.set(tMap).await()
                    // update blank ratings
                    createBlankRatings(tMap["timeslotId"] as String).forEach{ r ->
                        tsRef.collection("ratings").document().set(r.toMap()).await()
                    }
                }
            }
            true
        } catch(fe: FirebaseException){
            fe.printStackTrace()
            false
        } catch(e: Exception){
            e.printStackTrace()
            false
        }
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
        if (timeslotId == null)
            return false
        try {
            viewModelScope.launch {
                val tsRef = db.collection("timeslots").document(timeslotId)
                // delete ratings
                tsRef.collection("ratings").get().await().documents.forEach{ r -> r.reference.delete().await() }
                val chatRefs = tsRef.collection("chats")
                chatRefs.get().await().documents.forEach { cs ->
                    val messages = cs.reference.collection("messages").get().await()
                    // delete messages
                    messages.forEach { m -> m.reference.delete().await() } // delete messages
                    // delete chats
                    cs.reference.delete().await() // delete chats
                }
                // delete timeslot
                tsRef.delete().await()
            }
            return true
        } catch(e: Exception){
            e.printStackTrace()
            return false
        }
    }

    /**
     * Add chat between the owner of the timeslot (PUBLISHER) with the given id and the current user (CLIENT)
     **/
    fun addChat(timeslotId: String): Boolean {
        return try {
            viewModelScope.launch {
                val userRef = db.collection("users").document(auth.currentUser!!.uid)
                val clientMap = userRef.get().await().let{ Utils.toUserMap(it) } ?: throw Exception("clientMap shouldn't be null")
                val chatRef = db.collection("timeslots").document(timeslotId).collection("chats").document()
                val chatId = chatRef.id
                val newChat = Chat(chatId, clientMap, false, mutableListOf())
                // add chat to db with id chatId
                db.collection("chats").document(chatId).set(newChat.toMap()).await()
            }
            true
        } catch(e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun setChatAssigned(chatId: String, assigned: Boolean): Boolean {
        return try {
            viewModelScope.launch {
                val chat = db.collectionGroup("chats").whereEqualTo("chatId", chatId).get().await() ?: throw Exception("chatId ($chatId) not found")
                if(chat.documents.size != 1) throw Exception("chatId ($chatId) should be unique")
                chat.documents[0].reference.update("assigned", assigned).await()
            }
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun addMessage(chatId: String, text: String): Boolean {
        return try {
            viewModelScope.launch {
                val userId = auth.currentUser!!.uid
                val chats = db.collectionGroup("chats").whereEqualTo("chatId", chatId).get().await()
                if(chats.documents.size != 1)
                    throw Exception("only one chat should have chatId $chatId")
                val chat = chats.documents[0]
                val clientId = (chat["client"] as Map<String, Any>)["userId"] as String
                val sender = when (clientId) {
                    userId -> Message.Sender.CLIENT
                    else -> Message.Sender.PUBLISHER
                }
                val ts = Timestamp.now()
                val msgRef = chat.reference.collection("messages")
                val msgId = msgRef.document().id
                val newMessage = Message(msgId, text, ts, sender)
                msgRef.document(msgId).set(newMessage).await()
            }
            true
        } catch(e: Exception){
            e.printStackTrace()
            false
        }
    }

    // update rating (Ratings are created at Timeslot creation, so they must exist here)
    fun updateRating(timeslotId: String, rating: Int, comment: String): Boolean {
        return try {
            viewModelScope.launch {
                val userId = auth.currentUser!!.uid
                val ts = db.collection("timeslots").document(timeslotId).get().await()
                val ratings = ts.reference.collection("ratings").get().await()
                if(ratings.documents.size != 2)
                    throw Exception("timeslot must contain exactly two ratings (${ratings.documents.size} found)")
                val tsMap = Utils.toTimeslotMap(ts) ?: throw Exception("timeslot map creation failed")
                val publisherId = (tsMap["publisher"] as Map<String, Any>)["userId"] as String
                val by = when(publisherId) {
                    userId -> Message.Sender.PUBLISHER
                    else -> Message.Sender.CLIENT
                }
                ratings.documents.forEach { rs ->
                    val ratingMap = Utils.toRatingMap(rs) ?: throw Exception("rating map creation failed")
                    if(ratingMap["sender"] as Message.Sender == by){
                        rs.reference.update("rating", rating, "comment", comment).await()
                    }
                }
            }
            true
        } catch(e: Exception){
            e.printStackTrace()
            false
        }
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
            if( m == null ) throw Exception("E")
            viewModelScope.launch {
                m.forEach { ms ->
                    val chat = ms.reference.parent.get().await()
                    val tId = chat.documents.map{ cs -> cs.reference.parent.id }[0]
                    timeslots.value?.find{ t -> t.timeslotId == tId }.apply{  }
                    ms.id
                }
            }
        }
    }

}