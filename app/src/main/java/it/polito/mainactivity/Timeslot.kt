package it.polito.mainactivity

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.DateFormat
import java.time.DayOfWeek
import java.util.*

data class Timeslot (val title:String,
                     val description:String,
                     val date: Calendar,
                     val startHour : String,
                     val endHour:String,
                     val location:String,
                     val category:String ){

    var repetition:String =""
    var days: List<Int> = listOf()
    var endRepetitionDate:Calendar?= null
    var dates: MutableList<Calendar> = mutableListOf()

    var dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY)

    constructor(title:String,
                 description:String,
                 date: Calendar,
                 startHour : String,
                 endHour:String,
                 location:String,
                 category:String,
                 _repetition: String,
                _days: List<Int>,
                _endRepetitionDate: Calendar): this(title, description, date, startHour, endHour, location, category){
                    repetition = _repetition
                    days = _days
        endRepetitionDate = if(_endRepetitionDate.before(date)){
            date
        }else {
            _endRepetitionDate
        }
                    createDates()
                }

    private fun createDates() {
        var tmp = Calendar.getInstance()
        tmp.timeInMillis = date.timeInMillis
        if(repetition == "") return
        else if(repetition.lowercase()=="weekly"){
            while(tmp.before(endRepetitionDate)){
                if(days.contains(tmp.get(Calendar.DAY_OF_WEEK))){
                    dates.add(tmp)
                }
                //we bring tmp to the next day
                tmp.add(Calendar.DATE,1 )
            }
        }else{ //monthly
            while(tmp.before(endRepetitionDate)){
                dates.add(tmp)
                //we bring tmp to the next day
                tmp.add(Calendar.MONTH,1 )
            }
        }
    }

    private fun getDayName(num : Int):String{
        when (num) {
            Calendar.SUNDAY -> return "Sunday"
            Calendar.MONDAY -> return "Monday"
            Calendar.TUESDAY -> return "Tuesday"
            Calendar.WEDNESDAY -> return "Wednesday"
            Calendar.THURSDAY -> return "Thursday"
            Calendar.FRIDAY -> return "Friday"
            Calendar.SATURDAY -> return "Saturday"
            else -> return ""
        }
    }

    fun getDaysOfRepetition():String{
        return days.joinToString(", ","","",-1, "...") { getDayName(it) }
    }
}
