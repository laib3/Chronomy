package it.polito.mainactivity.model

import org.json.JSONArray
import org.json.JSONObject
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

    }

}