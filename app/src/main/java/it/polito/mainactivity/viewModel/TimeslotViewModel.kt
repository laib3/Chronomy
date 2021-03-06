@file:Suppress("PrivatePropertyName")

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
import com.google.firebase.firestore.*
import it.polito.mainactivity.R
import it.polito.mainactivity.model.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class TimeslotViewModel(application: Application) : AndroidViewModel(application) {

    private val TIME_LENGTH: Int = 5

    private val _timeslots = MutableLiveData<List<Timeslot>>()
    val timeslots: LiveData<List<Timeslot>> = _timeslots

    private val _newChatId = MutableLiveData<String?>()
    val newChatId: LiveData<String?> = _newChatId

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
                        // TODO consider also timeslot delete
                        if (tsQuery != null && tsQuery.documents.size > 0) {
                            viewModelScope.launch {
                                _timeslots.value =
                                    tsQuery.filterNotNull().map { ts ->
                                        val timeslotMap = Utils.toTimeslotMap(ts)!!
                                        val publisher = ts.get("publisher") as Map<String, Any>
                                        val chatsQuery =
                                            ts.reference.collection("chats").get().await()
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
                                        val t =
                                            Timeslot(
                                                timeslotMap,
                                                publisher,
                                                ratings,
                                                chats,
                                                clients,
                                                messages
                                            )
                                        t
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
                if (rQuery == null)
                    throw Exception("E")
                if (rQuery.documents.size > 0) {
                    val tmpTimeslots: MutableList<Timeslot>? = _timeslots.value?.toMutableList()
                    rQuery.forEach { rs ->
                        // find timeslot which contains the rating
                        val timeslot =
                            _timeslots.value?.find { timeslot -> timeslot.timeslotId == rs.reference.parent.parent?.id }
                        val timeslotId = timeslot?.timeslotId
                        if (timeslot != null) {
                            val newRating = Rating(Utils.toRatingMap(rs)!!)
                            val newRatings = timeslot.ratings
                            val oldRating = newRatings.find { rating -> rating.by == newRating.by }
                            if (oldRating == null) { // add new rating if there wasn't
                                newRatings.add(newRating)
                            } else { // update old ratings list
                                newRatings.apply { map { rating -> if (rating.by == newRating.by) newRating else rating } }
                            }
                            tmpTimeslots?.map{ t -> if(t.timeslotId == timeslotId) t.apply{this.ratings = newRatings} else t}
                        }
                    }
                    tmpTimeslots?.let{ _timeslots.value = it }
                }
            }

        chatsListenerRegistration =
            db.collectionGroup("chats").addSnapshotListener { cQuery, error ->
                if (cQuery == null)
                    throw Exception("E")
                // updated value of chats
                val chatsMaps = cQuery.documents.map{ c -> Utils.toChatMap(c) }
                val chatIds = chatsMaps.map{ c -> c?.get("chatId") as String }
                if(cQuery.documents.size == 0){ // chat may have been deleted
                    // keep only chats which are in the snapshot
                    val ts = _timeslots.value
                    if(ts != null) {
                        _timeslots.value = ts
                            .map { t ->
                                t.apply {
                                    this.chats =
                                        this.chats.filter { c -> c.chatId in chatIds }
                                            .toMutableList()
                                }
                            }
                    }
                }
                else if (cQuery.documents.size > 0) { // also here...
                    val tmpTimeslots: MutableList<Timeslot>? = _timeslots.value?.toMutableList()
                    cQuery.forEach { cs ->
                        // first parent is the collection of chats, second is the timeslot
                        val timeslot =
                            _timeslots.value?.find { t -> t.timeslotId == cs.reference.parent.parent?.id }
                        val timeslotId = timeslot?.timeslotId
                        if (timeslot != null) {
                            val chatMap = Utils.toChatMap(cs) // chat being examinated
                            val chats = timeslot.chats
                            // val chatMap = Utils.toChatMap(cs)
                            val oldChat = chats.find { chat -> chat.chatId == chatMap?.get("chatId") as String }
                            val messages = oldChat?.messages ?: listOf()
                            val newChat = chatMap?.let { Chat(chatMap, messages.map{m -> m.toMap()}, chatMap["client"] as Map<String, Any>) }
                            if (oldChat == null && newChat != null) { // add new chat if there wasn't
                                chats.add(newChat)
                            } else if(newChat != null){ // update old chats list
                                chats.apply { map { chat -> if (chat.chatId == newChat.chatId) newChat else chat } }
                            }
                            tmpTimeslots?.map{ t -> if(t.timeslotId == timeslotId) t.apply{ this.chats = chats } else t }
                        }
                    }
                    tmpTimeslots?.let{ _timeslots.value = it.map{ t -> t.apply{this.chats = this.chats.filter{ c -> c.chatId in chatIds }.toMutableList() } }
                }
            }
        }

        messagesListenerRegistration =
            db.collectionGroup("messages").addSnapshotListener { mQuery, error ->
                if (mQuery == null)
                    throw Exception("query result for messages shouldn't be empty")
                if (mQuery.documents.size > 0) {
                    val tmpTimeslots: MutableList<Timeslot>? = _timeslots.value?.toMutableList()
                    mQuery.forEach { ms ->
                        // first parent is the collection of messages, second parent is the chat document
                        // third parent is the collection of chats, fourth is the timeslot document
                        val timeslotId = ms.reference.parent.parent!!.parent.parent!!.id
                        val chatId = ms.reference.parent.parent!!.id
                        val timeslot = _timeslots.value?.find { t -> t.timeslotId == timeslotId }
                        val newMessage = Utils.toMessageMap(ms)?.let { Message(it) }
                        if(newMessage != null){
                            if (timeslot != null) {
                                val chats = timeslot.chats
                                val chat = chats.find { c -> c.chatId == chatId }
                                if (chat != null) {
                                    val oldMessages = chat.messages
                                    if (oldMessages.find { m -> m.messageId == newMessage.messageId } == null)
                                        oldMessages.add(newMessage)
                                    else
                                        oldMessages.apply { map { m -> if (m.messageId == newMessage.messageId) newMessage else m } }
                                    chats.apply {
                                        find { c -> c.chatId == chatId }?.messages = oldMessages
                                    }
                                    tmpTimeslots?.map{ t -> if(t.timeslotId == timeslotId) t.apply{ this.chats = chats } else t }
                                }
                            }
                        }
                    }
                    tmpTimeslots?.let{ _timeslots.value = it }
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
        // immediate feedback: update local ViewModel before getting updates from the db
        this._timeslots.value = this._timeslots.value?.map{ t ->
            if(t.timeslotId != timeslotId)
                t
            else
                when(field){
                    "title" -> t.apply{title = newValue as String}
                    "description" -> t.apply{description = newValue as String}
                    "date" -> t.apply { date = newValue as Calendar }
                    "startHour" -> t.apply { startHour = newValue as String }
                    "endHour" -> t.apply{ endHour = newValue as String }
                    "location" -> t.apply { location = newValue as String }
                    "category" -> t.apply { category = newValue as String }
                    else -> t
                }
        }
        // update the db
        db
            .collection("timeslots")
            .document(timeslotId)
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
    fun resetSubmitFields(): Boolean {
        return try {
            viewModelScope.launch {
                val userId = auth.currentUser!!.uid
                val currentUser = db.collection("users").document(userId).get().await()
                _submitTimeslot.value = Timeslot(
                    Utils.toUserMap(currentUser) ?: throw Exception("user map creation failed")
                ) // default constructor
                _submitRepetitionType.value = null
                _submitDaysOfWeek.value =
                    listOf(GregorianCalendar.getInstance().get(Calendar.DAY_OF_WEEK))
                _submitEndRepetitionDate.value = GregorianCalendar.getInstance()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
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
            "status" to t.status
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
            val timeslotMaps: List<Map<String, Any>>  =
                dates.map { date ->
                    val timeslotId = db.collection("timeslots").document().id
                    createSubmitTimeslotMap(t, date, timeslotId)
                }
            val listOfTimeslotIds = timeslotMaps.map{ tm -> tm["timeslotId"] as String }
            viewModelScope.launch {
                // create one timeslot for each date
                timeslotMaps
                    .forEach { tMap ->
                        val tsRef = db.collection("timeslots").document(tMap["timeslotId"] as String)
                        db.runBatch{ batch ->
                            // update timeslot
                            batch.set(tsRef, tMap)
                            // update ratings
                            createBlankRatings(tMap["timeslotId"] as String).forEach{ r ->
                                val rRef = tsRef.collection("ratings").document()
                                batch.set(rRef, r.toMap())
                            }
                        }.await()
                    }
            }
            val newTimeslots = _timeslots.value?.toMutableList()
            if(newTimeslots != null){
                timeslotMaps.forEach{ tm ->
                    val tmp = Timeslot(tm, t.publisher, t.ratings.map{r -> r.toMap()}.toMutableList(), t.chats.map{c -> c.toMap()}, t.chats.map{c -> c.client}, t.chats.map{ c -> c.messages.map{m -> m.toMap()} })
                    newTimeslots.add(tmp)
                }
                _timeslots.value = newTimeslots!!
            }
            true
        } catch (fe: FirebaseException) {
            fe.printStackTrace()
            false
        } catch (e: Exception) {
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

    private fun checkSubmitValid(): Boolean {
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
                val ratingsRefs = tsRef.collection("ratings").get()
                        .await()
                val chatsMessages = hashMapOf<DocumentSnapshot, MutableList<DocumentSnapshot>>()
                val chatsRefs = tsRef.collection("chats").get().await()
                chatsRefs.documents.forEach{ cs ->
                    val messages = cs.reference.collection("messages").get().await()
                    chatsMessages.put(cs, messages.documents.toMutableList())
                }
                db.runTransaction { transaction ->
                    // delete ratings
                    ratingsRefs.documents.forEach { r -> transaction.delete(r.reference) }
                    chatsMessages.entries.forEach{ entry ->
                        // delete messages
                        entry.value.forEach{ ms -> transaction.delete(ms.reference) }
                        // delete chat
                        transaction.delete(entry.key.reference)
                    }
                    transaction.delete(tsRef)
                }.await()
            }
            // update live viewmodel
            _timeslots.value = _timeslots.value?.filter{ t -> t.timeslotId != timeslotId }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Add chat between the owner of the timeslot (PUBLISHER) with the given id and the current user (CLIENT)
     **/
    fun addChat(timeslotId: String, client: User): Boolean {
        return try {
            val clientMap = client.toMap()
            val chatRef = db.collection("timeslots").document(timeslotId).collection("chats").document()
            val chatId = chatRef.id
            val newChat = Chat(chatId, clientMap, false, mutableListOf())
            viewModelScope.launch {
                // add chat to db with id chatId
                chatRef.set(newChat.toMap()).await()
                _newChatId.value = chatId
            }
            true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun setChatAssigned(chatId: String, assigned: Boolean): Boolean {
        return try {
            val chatRef = db.collectionGroup("chats").whereEqualTo("chatId", chatId)
            viewModelScope.launch {
                val chat = chatRef.get().await()
                    ?: throw Exception("chatId ($chatId) not found")
                if (chat.documents.size != 1) throw Exception("chatId ($chatId) should be unique")
                chat.first().reference.update("assigned", assigned).await()
                val timeslot = chat.first().reference.parent.parent!!.get().await()
                val cost = timeslot.let{ Utils.tcuFromStartEndHour(it["startHour"] as String, it["endHour"] as String) }
                val clientId = ((chat.first().reference.get().await()["client"] as Map<String, Any>)["userId"] as String)
                val clientRef = db.collection("users").document(clientId)
                val clientBalance = (clientRef.get().await().get("balance") as Long).toInt()
                val publisherId = (timeslot.get("publisher") as Map<String, Any>)["userId"] as String
                val publisherRef = db.collection("users").document(publisherId)
                if(assigned && clientBalance >= cost){
                    db.runTransaction { transaction ->
                        val publisherBalance = (transaction.get(publisherRef).get("balance") as Long).toInt()
                        transaction.update(publisherRef, "balance", publisherBalance + cost)
                        transaction.update(clientRef, "balance", clientBalance - cost)
                        chat.first().reference.parent.parent!!.update("status", Timeslot.Status.ASSIGNED)
                    }.await()
                }
                else {
                    chat.first().reference.parent.parent!!.update("status", Timeslot.Status.PUBLISHED).await()
                    reject(chatId)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun reject(chatId: String?){
        if(chatId == null)
            return
        try {
            viewModelScope.launch {
                val chatQuery = db.collectionGroup("chats").whereEqualTo("chatId", chatId)
                val chats = chatQuery.get().await().documents
                if(chats.isNotEmpty()){
                    val chat = chats.first()
                    val messagesRef = chat.reference.collection("messages").get().await()
                    db.runTransaction { transaction ->
                        messagesRef.documents.forEach{ ms -> transaction.delete(ms.reference) }
                        chat.reference.delete()
                    }.await()
                }
                // TODO also update live viewmodel
            }
        } catch(e: Exception){
            e.printStackTrace()
        }
    }

    fun addMessage(chatId: String, text: String): Boolean {
        return try {
            viewModelScope.launch {
                val userId = auth.currentUser!!.uid
                val chats = db.collectionGroup("chats").whereEqualTo("chatId", chatId).get().await()
                if (chats.documents.size != 1)
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
                val mMap = newMessage.toMap()
                mMap["timestamp"] = FieldValue.serverTimestamp()
                msgRef.document(msgId).set(mMap).await()
            }
            true
        } catch (e: Exception) {
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
                if (ratings.documents.size != 2)
                    throw Exception("timeslot must contain exactly two ratings (${ratings.documents.size} found)")
                val tsMap =
                    Utils.toTimeslotMap(ts) ?: throw Exception("timeslot map creation failed")
                val status = Timeslot.Status.valueOf(tsMap["status"] as String)
                if (status != Timeslot.Status.COMPLETED)
                    throw Exception("timeslot must be completed!")
                val publisherId = (ts["publisher"] as Map<String, Any>)["userId"] as String
                val by = when (publisherId) {
                    userId -> Message.Sender.PUBLISHER
                    else -> Message.Sender.CLIENT
                }
                ratings.documents.forEach { rs ->
                    val ratingMap =
                        Utils.toRatingMap(rs) ?: throw Exception("rating map creation failed")
                    if (Message.Sender.valueOf(ratingMap["by"] as String) == by) {
                        rs.reference.update("rating", rating, "comment", comment).await()
                    }
                }
            }
            true
        } catch (e: Exception) {
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

    fun getMessages(messagesQuery: QuerySnapshot): MutableList<Map<String, Any>> {
        return messagesQuery.documents
            .map { m -> Utils.toMessageMap(m)!! }
            .toMutableList()
    }

    fun getRatings(ratingsQuery: QuerySnapshot): MutableList<Map<String, Any>> {
        return ratingsQuery.documents
            .map { r -> Utils.toRatingMap(r)!! }
            .toMutableList()
    }

    fun resetNewChatId() {
        _newChatId.value = null
    }

}