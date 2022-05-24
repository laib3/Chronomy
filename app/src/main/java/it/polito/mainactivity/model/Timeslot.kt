package it.polito.mainactivity.model

import java.util.*

data class Timeslot(
    var user: User
) {

    var tid: String
    var title: String
    var description: String
    var startDate: Calendar
    var startHour: String
    var endHour: String
    var location: String
    var category: String
    var repetition: String? = null
    var days: List<Int>
    var endRepetitionDate: Calendar

    // private var dates: MutableList<Calendar> = mutableListOf()

    init {
        val hour = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val min = GregorianCalendar.getInstance().get(Calendar.MINUTE)
        val timeText = Utils.formatTime(hour, min)
        val currentDate = GregorianCalendar.getInstance()
        val currentDay = currentDate.get(Calendar.DAY_OF_WEEK)
        tid = ""
        title = ""
        description = ""
        startDate = currentDate
        startHour = timeText
        endHour = timeText
        location = ""
        category = "Other"
        repetition = null
        days = listOf(currentDay)
        endRepetitionDate = currentDate
        // createDates()
    }

    constructor(
        _tid: String,
        _title: String,
        _description: String,
        _startDate: Calendar,
        _startHour: String,
        _endHour: String,
        _location: String,
        _category: String,
        _repetition: String?,
        _days: List<Int>,
        _endRepetitionDate: Calendar,
        _user: User
    ) : this(_user) {
        tid = _tid
        title = _title
        description = _description
        startDate = _startDate
        startHour = _startHour
        endHour = _endHour
        location = _location
        category = _category
        repetition = _repetition
        days = _days
        endRepetitionDate = _endRepetitionDate
        endRepetitionDate = if (endRepetitionDate.before(startDate)) {
            startDate
        } else {
            endRepetitionDate
        }
        // createDates()
    }

    override fun toString(): String {
        val sRepetition = if (repetition == null) "null" else "\"$repetition\""
        return """
        {
        "user": ${user},
        "id": "$tid",
        "title": "$title", 
        "description": "$description",
        "startDate": 
            {"year": ${startDate.get(Calendar.YEAR)}, 
            "month": ${startDate.get(Calendar.MONTH)}, 
            "day": ${startDate.get(Calendar.DAY_OF_MONTH)}},
        "startHour": "$startHour", 
        "endHour": "$endHour", 
        "location": "$location", 
        "category": "$category", 
        "repetition": $sRepetition,
        "days": $days,
        "endRepetitionDate": 
            {"year": ${endRepetitionDate.get(Calendar.YEAR)},
            "month": ${endRepetitionDate.get(Calendar.MONTH)},
            "day": ${endRepetitionDate.get(Calendar.DAY_OF_MONTH)}
            }
        },
        """.replace("\n", "").trimIndent()
    }

    // TODO remove because it's wrong and not used
    /*
    private fun createDates() {
        val tmp = Calendar.getInstance()
        tmp.timeInMillis = startDate.timeInMillis
        when {
            repetition?.lowercase() == "weekly" -> {
                while (tmp.before(endRepetitionDate)) {
                    if (days.contains(tmp.get(Calendar.DAY_OF_WEEK))) {
                        dates.add(tmp)
                    }
                    //we bring tmp to the next day
                    tmp.add(Calendar.DATE, 1)
                }
            }
            repetition?.lowercase() == "monthly" -> { //monthly
                while (tmp.before(endRepetitionDate)) {
                    dates.add(tmp)
                    //we bring tmp to the next day
                    tmp.add(Calendar.MONTH, 1)
                }
            }
            else -> return
        }
    }
     */

}