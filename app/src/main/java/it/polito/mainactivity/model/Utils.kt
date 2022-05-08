package it.polito.mainactivity.model

import it.polito.mainactivity.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.util.*

class Utils {

    companion object {

        fun JSONArrayToList(ja: JSONArray): MutableList<JSONObject>{
            val list: MutableList<JSONObject> = mutableListOf()
            for(i in 0 until ja.length()){
                list.add(ja.getJSONObject(i))
            }
            return list
        }

        fun JSONArrayToIntList(ja: JSONArray): MutableList<Int>{
            val list: MutableList<Int> = mutableListOf()
            for(i in 0 until ja.length()){
                list.add(ja.getInt(i))
            }
            return list
        }

        fun JSONObjectToTimeslot(jo: JSONObject): Timeslot {
                val title: String = jo.getString("title")
                val description: String = jo.getString("description")
                val date: JSONObject = jo.getJSONObject("date")
                val dYear: Int = date.getInt("year")
                val dMonth: Int = date.getInt("month")
                val dDay: Int = date.getInt("day")
                val startHour: String = jo.getString("startHour")
                val endHour: String = jo.getString("endHour")
                val location: String = jo.getString("location")
                val category: String = jo.getString("category")
                val repetition: String = jo.getString("repetition")
                val JSONDays: JSONArray = jo.getJSONArray("days")
                val days: MutableList<Int> = JSONArrayToIntList(JSONDays)
                val endRepetitionDate: JSONObject = jo.getJSONObject("endRepetitionDate")
                val erYear: Int = endRepetitionDate.getInt("year")
                val erMonth: Int = endRepetitionDate.getInt("month")
                val erDay: Int = endRepetitionDate.getInt("day")
                return Timeslot(title, description, GregorianCalendar(dYear, dMonth, dDay),
                    startHour, endHour, location, category, repetition,
                    days, GregorianCalendar(erYear, erMonth, erDay))
            }

        fun formatDateToString(date: Calendar?): String {
            if(date == null)
                return ""
            var dateFormat:DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY)
            dateFormat.timeZone = date.timeZone
            return dateFormat.format(date.time)
        }

        fun getSkillImgRes(title: String) : Int?{
            return when(title){
                "Gardening" -> R.drawable.ic_skill_gardening
                "Tutoring" -> R.drawable.ic_skill_tutoring
                "Child Care" -> R.drawable.ic_skill_child_care
                "Odd Jobs" -> R.drawable.ic_skill_odd_jobs
                "Home Repair" -> R.drawable.ic_skill_home_repair
                "Wellness" -> R.drawable.ic_skill_wellness
                "Delivery" -> R.drawable.ic_skill_delivery
                "Transportation" -> R.drawable.ic_skill_transportation
                "Companionship" -> R.drawable.ic_skill_companionship
                "Other" -> R.drawable.ic_skill_other
                else -> null
            }
        }
    }

}