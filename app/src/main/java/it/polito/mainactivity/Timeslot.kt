package it.polito.mainactivity

import java.util.*

data class Timeslot (val title:String,
                     val description:String,
                     val date: Date,
                     val startHour : String,
                     val endHour:String,
                     val location:String,
                     val category:String ){

    constructor(title:String,
                 description:String,
                 date: Date,
                 startHour : String,
                 endHour:String,
                 repetition: String,
                 days: List<Int>,
                 endRepetitionDate: Date,
                 dates: List<Date>,
                 location:String,
                 category:String ): this(title, description, date, startHour, endHour, location, category)

}
