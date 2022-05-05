package it.polito.mainactivity.model

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import java.io.*

enum class Field {
    NAME, SURNAME, NICKNAME, BIO, EMAIL, PHONE, LOCATION, BALANCE
}

class UserProfileModel(val application: Application) {

    // no database here
    private val SHARED_PREFERENCES_NAME: String = "userprofilesharedpreferences"
    private val PROFILE_TAG: String = "profile"
    private val PICTURE_DIR_PATH: String = "images"
    private val PROFILE_PICTURE_NAME: String = "profile.jpg"
    private val sharedPreferences = application.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

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

    init {
        loadProfile()
    }

    override fun toString() = """{ "name": "$name", "surname": "$surname", "nickname": "$nickname", "bio": "$bio", """ +
            """"email": "$email", "phone": "$phone", "location": "$location", "balance": $balance, "skills": $skills }"""
    // load profile from shared preferences (or create fake content)
    private fun loadProfile(){
        // sharedPreferences shouldn't be null, but just in case...
        // retrieve profile as a JSON string
        val profileString: String? = sharedPreferences?.getString(PROFILE_TAG, null)
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
            // if null then create fake example skills
            null -> createSkills()
            // if not null return a JSONArray
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
        try {
            val imageDirectory: File = application.getDir(PICTURE_DIR_PATH, Context.MODE_PRIVATE)
            val file = File(imageDirectory, PROFILE_PICTURE_NAME)
            val b: Bitmap = BitmapFactory.decodeStream(FileInputStream(file))
            picture = BitmapDrawable(application.applicationContext.resources, b)
        } catch(e: FileNotFoundException){
            picture = null
        }
    }

    fun setData(s: String, f: Field){
        when(f){
            Field.NAME -> name = s
            Field.SURNAME -> surname = s
            Field.BIO -> bio = s
            Field.NICKNAME -> nickname = s
            Field.EMAIL -> email = s
            Field.PHONE -> phone = s
            Field.LOCATION -> location = s
            Field.BALANCE -> balance = s.toInt()
        }
        // update profile string
        sharedPreferences.edit().putString(PROFILE_TAG, this.toString()).apply()
    }
    fun setSkills(s: List<Skill>) {
        skills = s.toMutableList()
        sharedPreferences.edit().putString(PROFILE_TAG, this.toString()).apply()
    }
    fun setPicture(d: Drawable){
        picture = d
        savePicture()
    }

    fun getData(f: Field): MutableLiveData<String>{
        return when(f){
            Field.NAME -> MutableLiveData(name)
            Field.SURNAME -> MutableLiveData(surname)
            Field.NICKNAME -> MutableLiveData(nickname)
            Field.BIO -> MutableLiveData(bio)
            Field.EMAIL -> MutableLiveData(email)
            Field.PHONE -> MutableLiveData(phone)
            Field.LOCATION -> MutableLiveData(location)
            Field.BALANCE -> MutableLiveData(balance.toString())
        }
    }
    fun getSkills(): MutableLiveData<List<Skill>> = MutableLiveData(skills)
    fun getPicture(): MutableLiveData<Drawable> = MutableLiveData(picture)

    // create fake example skills
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

    private fun savePicture(): String {
        val imageDirectory: File = application.getDir(PICTURE_DIR_PATH, Context.MODE_PRIVATE)
        val imagePath: File = File(imageDirectory, PROFILE_PICTURE_NAME)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(imagePath)
            val QUALITY = 100
            picture?.toBitmap()?.compress(Bitmap.CompressFormat.PNG, QUALITY, outputStream)
        } catch(e:Exception){
            e.printStackTrace()
        } finally {
            try {
                outputStream?.close()
            } catch(e: IOException){
                e.printStackTrace()
            }
        }
        return imageDirectory.absolutePath
    }

}