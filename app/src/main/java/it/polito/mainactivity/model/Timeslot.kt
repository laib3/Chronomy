package it.polito.mainactivity.model

import java.util.*

data class Timeslot(
    var publisher: User
) {

    enum class Status {PUBLISHED, ASSIGNED, COMPLETED}

    var timeslotId: String
    var title: String
    var description: String
    var date: Calendar
    var startHour: String
    var endHour: String
    var location: String
    var category: String
    var status: Status
    val chats: MutableList<Chat>
    val ratings: MutableMap<String, Rating>

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
        ratings = mutableMapOf() // map is empty, gets populated later
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
        _publisher: User,
    ) : this(_publisher) {
        timeslotId = _timeslotId
        title = _title
        description = _description
        date = _date
        startHour = _startHour
        endHour = _endHour
        location = _location
        category = _category
        // createDates()
    }

    override fun toString(): String {
        return """
        {
        "timeslotId": "$timeslotId",
        "publisher": ${publisher},
        "title": "$title", 
        "description": "$description",
        "date": 
            {"year": ${date.get(Calendar.YEAR)}, 
            "month": ${date.get(Calendar.MONTH)}, 
            "day": ${date.get(Calendar.DAY_OF_MONTH)}},
        "startHour": "$startHour", 
        "endHour": "$endHour", 
        "location": "$location", 
        "category": "$category", 
        },
        """.replace("\n", "").trimIndent()
    }

}