package it.polito.mainactivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.protobuf.StringValue
import it.polito.mainactivity.model.Utils
import java.util.*

class MainViewModel: ViewModel() {
    //private val _users= MutableLiveData<List<User>>()
    //val users: LiveData<List<User>> = _users

    private val _timeslots= MutableLiveData<List<Timeslot>>()
    val timeslots: LiveData<List<Timeslot>> = _timeslots

    private val db: FirebaseFirestore
    //private var lUsers: ListenerRegistration
    private var lTimeslots: ListenerRegistration

    init {
        db = FirebaseFirestore.getInstance()
        /*
        lUsers = db.collection("users")
            .addSnapshotListener { v, e ->
                if (e==null) {
                    _users.value = v!!.mapNotNull { d -> d.toUser() }
                    Log.d("USER", _users.value.toString())
                } else _users.value = emptyList()
            }
        */
        lTimeslots = db.collection("timeslots")
            .addSnapshotListener { v, e ->
                if (e==null) {
                    _timeslots.value = v!!.mapNotNull { d -> d.toTimeslot() }
                    Log.d("TIMESLOT", _timeslots.value.toString())
                } else _timeslots.value = emptyList()
            }

    }

    override fun onCleared() {
        super.onCleared()
        //lUsers.remove()
        lTimeslots.remove()
    }
}
/*
data class User (
    var uid: String,
    var name: String,
    var surname: String,
    var nickname: String,
    var bio: String,
    var email: String,
    var location: String,
    var phone: String,
    var skills: List<String>,
    var balance: Int,
    var timeslots: List<String>,
    var profilePicture: String?
)

fun DocumentSnapshot.toUser(): User? {
    return try {
        val name = get("name") as String
        val surname = get("surname") as String
        val nickname =  get("nickname") as String
        val bio = get("bio") as String
        val email = get("email") as String
        val location = get("location") as String
        val phone = get("phone") as String
        val skills = get("skills") as List<String>
        val balance = get("balance") as Int
        // TODO: ADD OTHER FIELDS

        User(id, name, surname, nickname, bio, email, location, phone, skills ,balance, listOf(), null)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

*/
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
        fun emptyTimeslot(): it.polito.mainactivity.model.Timeslot {
            val hour = GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val min = GregorianCalendar.getInstance().get(Calendar.MINUTE)
            val timeText = Utils.formatTime(hour, min)
            val date = GregorianCalendar.getInstance()
            val currentDay = date.get(Calendar.DAY_OF_WEEK)
            return it.polito.mainactivity.model.Timeslot(
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
fun DocumentSnapshot.toTimeslot(): Timeslot? {
    return try {
        val title = get("title") as String
        val description = get("description") as String
        // TODO: ADD OTHER FIELDS FROM DB
        val startDate = GregorianCalendar(2022, 5, 25)
        val startHour = get("startHour") as String
        val endHour = get("endHour") as String
        val location = get("location") as String
        val category = get("category") as String
        val repetition = null
        val days = listOf(GregorianCalendar(2022, 5, 25).get(Calendar.DAY_OF_WEEK))
        val endRepetitionDate = GregorianCalendar(2022, 5, 25)

        Timeslot(id, title, description, startDate, startHour, endHour,location, category, repetition, days, endRepetitionDate)

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
