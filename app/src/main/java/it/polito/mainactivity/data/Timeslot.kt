package it.polito.mainactivity.data

import it.polito.mainactivity.model.Utils
import java.util.*

data class Timeslot(
    var tid: String,
    var title: String,
    var description: String,
    var startDate: Calendar,
    var startHour : String,
    var endHour:String,
    var location:String,
    var category:String,
    var repetition: String?,
    var days: List<Int>,
    var endRepetitionDate: Calendar
){
    private var dates: MutableList<Calendar> = mutableListOf()

    init {
        endRepetitionDate = if(endRepetitionDate.before(startDate)){
            startDate
        } else {
            endRepetitionDate
        }
        createDates()
    }

    override fun toString(): String {
        val _sRepetition = if(repetition == null) "null" else "\"$repetition\""
        return """
        {
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
        "repetition": $_sRepetition,
        "days": $days,
        "endRepetitionDate": 
            {"year": ${endRepetitionDate.get(Calendar.YEAR)},
            "month": ${endRepetitionDate.get(Calendar.MONTH)},
            "day": ${endRepetitionDate.get(Calendar.DAY_OF_MONTH)}
            }
        }
        """.replace("\n", "").trimIndent()
    }

    private fun createDates() {
        val tmp = Calendar.getInstance()
        tmp.timeInMillis = startDate.timeInMillis
        when {
            repetition?.lowercase()=="weekly" -> {
                while(tmp.before(endRepetitionDate)){
                    if(days.contains(tmp.get(Calendar.DAY_OF_WEEK))){
                        dates.add(tmp)
                    }
                    //we bring tmp to the next day
                    tmp.add(Calendar.DATE, 1)
                }
            }
            repetition?.lowercase()=="monthly" -> { //monthly
                while(tmp.before(endRepetitionDate)){
                    dates.add(tmp)
                    //we bring tmp to the next day
                    tmp.add(Calendar.MONTH, 1)
                }
            }
            else -> return
        }
    }

    companion object {
        fun emptyTimeslot(): Timeslot {
            val hour = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val min = GregorianCalendar.getInstance().get(Calendar.MINUTE)
            val timeText = Utils.formatTime(hour, min)
            val date = GregorianCalendar.getInstance()
            val currentDay = date.get(Calendar.DAY_OF_WEEK)
            return Timeslot(
                "",
                "",
                "",
                date,
                timeText,
                timeText,
                "",
                "Other",
                null,
                listOf(currentDay),
                date
            )
        }
    }

}