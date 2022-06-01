package it.polito.mainactivity.model

import java.util.*

data class Timeslot(
    var publisher: Map<String, String>
) {

    enum class Status {PUBLISHED, ASSIGNED, COMPLETED, ERROR}

    var timeslotId: String
    var title: String
    var description: String
    var date: Calendar
    var startHour: String
    var endHour: String
    var location: String
    var category: String
    var status: Status
    var chats: MutableList<Chat>
    var ratings: MutableList<Rating>

    init {
        val hour = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val min = GregorianCalendar.getInstance().get(Calendar.MINUTE)
        val timeText = Utils.formatTime(hour, min)
        val currentDate = GregorianCalendar.getInstance()
        timeslotId = ""
        title = ""
        description = ""
        date = currentDate
        startHour = timeText
        endHour = timeText
        location = ""
        category = "Other"
        status = Status.PUBLISHED
        chats = mutableListOf()
        ratings = mutableListOf()
    }

    /* 2nd constructor */
    constructor(
        _timeslotId: String,
        _title: String,
        _description: String,
        _date: Calendar,
        _startHour: String,
        _endHour: String,
        _location: String,
        _category: String,
        _publisher: Map<String, String>,
        _status: Status,
        _chats: MutableList<Chat>,
        _ratings: MutableList<Rating>
    ) : this(_publisher) {
        timeslotId = _timeslotId
        title = _title
        description = _description
        date = _date
        startHour = _startHour
        endHour = _endHour
        location = _location
        category = _category
        publisher = _publisher
        status = _status
        chats = _chats
        ratings = _ratings
    }

    constructor(timeslotMap: Map<String, String>, _publisher: Map<String, String>, _ratings: List<Map<String, String>>,
                _chats: List<Map<String, String>>, _clients: List<Map<String, String>>,
                _messages: List<List<Map<String, String>>>): this(_publisher){
        timeslotId = timeslotMap["timeslotId"] ?: "null"
        title = timeslotMap["title"] ?: "null"
        description = timeslotMap["description"] ?: "null"
        date = timeslotMap["date"]?.let{ Utils.formatStringToDate(it) } ?: Calendar.getInstance()
        startHour = timeslotMap["startHour"] ?: "null"
        endHour = timeslotMap["endHour"] ?: "null"
        location = timeslotMap["location"] ?: "null"
        category = timeslotMap["category"] ?: "null"
        timeslotMap["status"]?.let{ Status.valueOf(it) } ?: Status.ERROR
        if(_ratings.size != 2)
            throw Exception("Error: ratings lenght should be equal to 2")
        ratings = _ratings.map{ rm -> Rating(rm) }.toMutableList()
        // perform some checks on lists length
        if(_chats.size != _clients.size || _clients.size != _messages.size || _messages.size != _chats.size)
            throw Exception("Error: chats, clients and messages must have same length")
        this.chats = _chats.mapIndexed{ i, cm -> Chat(cm, _messages[i], _clients[i]) }.toMutableList()
    }

    fun toMap(): Map<String, Any>{
        return hashMapOf(
            "timeslotId" to timeslotId,
            "title" to title,
            "description" to description,
            "date" to date,
            "startHour" to startHour,
            "endHour" to endHour,
            "location" to location,
            "category" to category,
            "status" to status,
            "chats" to chats.forEach{ it.toMap() },
            "publisher" to publisher
        )
    }

    override fun toString(): String {
        return """
        {
        "timeslotId": "$timeslotId",
        "publisher": ${publisher},
        "title": "$title", 
        "description": "$description",
        "date": "${Utils.formatDateToString(date)}",
        "startHour": "$startHour", 
        "endHour": "$endHour", 
        "location": "$location", 
        "category": "$category", 
        "status": "$status",
        "chats": "$chats",
        "ratings": "$ratings"
        },
        """.replace("\n", "").trimIndent()
    }


}