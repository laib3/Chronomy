package it.polito.mainactivity.model

import java.util.*

data class Timeslot(
    var user: User
) {

    var timeslotId: String
    var title: String
    var description: String
    var date: Calendar
    var startHour: String
    var endHour: String
    var location: String
    var category: String
    // var repetition: String? = null
    // var days: List<Int>
    // var submitEndRepetitionDate: Calendar

    // private var dates: MutableList<Calendar> = mutableListOf()

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
        // val currentDay = currentDate.get(Calendar.DAY_OF_WEEK)
        // repetition = null
        // days = listOf(currentDay)
        // submitEndRepetitionDate = currentDate
        // createDates()
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
        _user: User
    ) : this(_user) {
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
        "user": ${user},
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