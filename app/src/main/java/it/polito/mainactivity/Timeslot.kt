package it.polito.mainactivity

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
    var dates: List<Date> = listOf()


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
                    //dates = createDates()
                }

    /*private fun createDates(): List<Date> {
        if(repetition == "") return listOf()
        else{

        }
    }*/

}
