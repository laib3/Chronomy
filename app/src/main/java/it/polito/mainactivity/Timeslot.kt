package it.polito.mainactivity

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.DateFormat
import java.time.DayOfWeek
import java.util.*

data class Timeslot (val title:String,
                     val description:String,
                     val date: Date,
                     val startHour : String,
                     val endHour:String,
                     val location:String,
                     val category:String ){

    var repetition:String =""
    var days: List<Int> = listOf()
    var endRepetitionDate:Date?= null
    var dates: MutableList<Date> = mutableListOf()

    var dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.ITALY)

    constructor(title:String,
                 description:String,
                 date: Date,
                 startHour : String,
                 endHour:String,
                 location:String,
                 category:String,
                 _repetition: String,
                _days: List<Int>,
                _endRepetitionDate: Date): this(title, description, date, startHour, endHour, location, category){
                    repetition = _repetition
                    days = _days
                    endRepetitionDate = _endRepetitionDate
                    createDates()
                }

    private fun createDates() {
        if(repetition == "") return
        //TODO: CHECK if end > start
        // if(endRepetitionDate?.compareTo(date) < 0 ) return
        else{
            print(dateFormat.format(date))
            var tmp:Date = date
            var dayNumber:Int
            while(tmp <= endRepetitionDate){
                dayNumber = getDay(tmp)
                if(days.contains(dayNumber)){
                    dates.add(tmp)
                }
                //we being tmp to the next day
                tmp = Date(tmp.getTime() + 1000 * 60 * 60 * 24)
            }

        }
    }

    private fun getDay(date: Date?): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        //1 for monday, 7 for sunday
        return cal[Calendar.DAY_OF_WEEK]
    }
}
