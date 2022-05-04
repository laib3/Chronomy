package it.polito.mainactivity.model

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.util.Log
import it.polito.mainactivity.ui.userprofile.SkillViewModel
import org.json.JSONObject

class UserProfileModel(val application: Application) {

    // no database here
    private val sharedPreferencesName : String = "userprofilesharedpreferences"
    private val profileTag: String = "profile"

    private var name: String = ""
    private var surname: String = ""
    private var nickname: String = ""
    private var bio: String = ""
    private var email: String = ""
    private var phone: String = ""
    private var location: String = ""
    private var balance: Int = 0
    private var picture: Drawable? = null
    private var skills: MutableList<Skill> = mutableListOf()

    /* returns a JSON string representing the profile */
    override fun toString() = """{ "name": "$name", "surname": "$surname", "nickname": "$nickname", "bio": "$bio", """ +
                """"email": "$email", "phone": "$phone", "location": "$location", "balance": $balance, "skills: $skills }"""

    init {
        loadContent()
    }

    private fun saveContent(){
        val sharedPreferences = application.getSharedPreferences(sharedPreferencesName, MODE_PRIVATE)
    }

    // load content from shared preferences (or create fake content)
    private fun loadContent(){
        // sharedPreferences shouldn't be null, but just in case...
        val sharedPreferences : SharedPreferences? = application.getSharedPreferences(sharedPreferencesName, MODE_PRIVATE)
        // retrieve profile as a JSON string
        val profileString: String? = sharedPreferences?.getString(profileTag, null)
        val jsonProfile: JSONObject? = when(profileString) {
            null -> null
            else -> JSONObject(profileString)
        }
        name = jsonProfile?.getString("name") ?: "name"
        surname = jsonProfile?.getString("surname") ?: "surname"
        nickname = jsonProfile?.getString("nickname") ?: "nickname"
        bio = jsonProfile?.getString("bio") ?: "bio"
        email = jsonProfile?.getString("email") ?: "email"
        phone = jsonProfile?.getString("phone") ?: "phone"
        location = jsonProfile?.getString("location") ?: "location"
        balance = jsonProfile?.getInt("balance") ?: 0
        skills = when(jsonProfile){
            // null = create fake example skills
            null -> createSkills()
            // not null - returns a JSONArray
            else -> {
                val jsonArray = jsonProfile.getJSONArray("skills")
                val jsonSkills = mutableListOf<JSONObject>()
                // jsonArray is not iterable !!
                for(i in 0 until jsonArray.length())
                    jsonSkills.add(jsonArray.getJSONObject(i))
                jsonSkills
                    .map{ js -> Skill(js.getString("title")).apply{ active = js.getBoolean("active"); description = js.getString("description") } }
                    .toMutableList()
            }
        }
    }

    // create example fake skills
    private fun createSkills(): MutableList<Skill>{
        return mutableListOf(
            Skill("Gardening").apply { description = "I can mow the lawn, trim bushes, rake and pick up leaves in the garden."; active = true },
            Skill("Tutoring"),
            Skill("Child Care").apply { description = "I can take care of your children." },
            Skill("Odd Jobs"),
            Skill("Home Repair"),
            Skill("Wellness").apply { active = true },
            Skill("Delivery").apply { description = "I can bring you food."; active = true },
            Skill("Transportation"),
            Skill("Companionship"),
            Skill("Other")
        )
    }

}