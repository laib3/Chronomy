package it.polito.showprofileactivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONArray
import org.json.JSONObject


class ShowProfileActivity: AppCompatActivity() {

    private val SHPR_NAME:String = "sharedPreferences"
    private val PROFILE:String = "profile"

    lateinit var name:String
    lateinit var surname:String
    lateinit var nickname:String
    lateinit var bio:String
    lateinit var email:String
    lateinit var phone:String
    lateinit var location:String
    lateinit var skills:List<Skill>

    private lateinit var startForResult : ActivityResultLauncher<Intent>

    // extension function: convert a list of skills to a JSON string
    private fun skillsToJSON():String {
        return skills.map{s -> s.toJSON()}.joinToString(separator = "},{", prefix ="[{", postfix = "}]")
    }

    fun createSkills () : List<Skill>{
        // return a list of skills that contains all the titles present in skill.xml file
        // src of a skill is the name with spaces replaced with underscore and lowercase
        skills = mutableListOf()
        resources.getStringArray(R.array.skills_array).forEach{ s -> (skills as MutableList<Skill>).add(Skill(s, s.lowercase().replace(" ", "_"))) }
        // TODO: REMOVE
        // just static adding for testing
        skills.find { s -> s.title == "Gardening" }.apply {
            this?.active = true
            this?.description ="I can mow the lawn, trim bushes, rake and pick up leaves in the garden. I can also take care of watering the flowers and plants and putting fertilizer"
        }
        skills.find { s -> s.title == "Home Repair" }.apply {
            this?.active = true
            this?.description ="I can fix your home appliance"
        }
        skills.find { s -> s.title == "Child Care" }.apply {
            this?.active = true
            this?.description ="Babysit your kids"
        }
        skills.find { s -> s.title == "Transportation" }.apply {
            this?.active = true
        }
        return skills
    }

    // TODO remove
    // only for testing purposes
    private fun createProfile() {
        name = "Mario"
        surname = "Rossi"
        nickname = "supermario"
        bio = "Based in Italy. I love basketball, food and people. Simpatico, solare, in cerca di amicizie"
        email = "mariorossi@ymail.com"
        phone = "+39 3332030800"
        location = "Turin, Italy"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_profile_activity)

        // TODO remove
        createProfile()
        skills = createSkills()

        // TODO remove (the content should be only loaded from shared memory)
        saveContent()
        loadContent()
        updateView()

        val iv = findViewById<ImageView>(R.id.profilePicture)
        iv.clipToOutline = true

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val i = result.data
                // Handle the Intent
                name = (i?.getStringExtra(getString(R.string.key_name)) ?: "")
                surname = (i?.getStringExtra(getString(R.string.key_surname)) ?: "")
                nickname = (i?.getStringExtra(getString(R.string.key_nickname)) ?: "")
                bio = (i?.getStringExtra(getString(R.string.key_bio)) ?: "")
                email = (i?.getStringExtra(getString(R.string.key_email)) ?: "")
                phone = (i?.getStringExtra(getString(R.string.key_phone_number)) ?: "")
                location = (i?.getStringExtra(getString(R.string.key_location)) ?: "")
                saveContent()
                loadContent()
                updateView()
            }
        }
    }

    // save content to shared memory
    fun saveContent(){
        val sharedPreferences = getSharedPreferences(SHPR_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // put a profile string in the shared preferences
        val jsonSkills:String = skillsToJSON()
        val profileString:String = """{ "name": "$name", "surname":"$surname", "nickname":"$nickname", "bio":"$bio", "email":"$email", "phone":"$phone", "location":"$location", "skills":$jsonSkills }""".trimIndent()
        editor.putString(PROFILE, profileString)
        // save
        editor.apply()
    }

    // load content and update local variables
    fun loadContent(){
        val sharedPreferences = getSharedPreferences(SHPR_NAME, MODE_PRIVATE)
        val profile:String = sharedPreferences.getString(PROFILE, "profile") ?: "profile"
        val jobj = JSONObject(profile)

        name = jobj.getString("name")
        surname = jobj.getString("surname")
        nickname = jobj.getString("nickname")
        bio = jobj.getString("bio")
        email = jobj.getString("email")
        phone = jobj.getString("phone")
        location = jobj.getString("location")

        val array: JSONArray = jobj.getJSONArray("skills")
        val jobjects:MutableList<JSONObject> = mutableListOf()
        for(i in 0..array.length()-1){
            val jo = array.getJSONObject(i)
            jobjects.add(jo)
        }
        skills = jobjects.map{ jo -> Skill(jo.getString("title"), jo.getString("src"), jo.getBoolean("active"), jo.getString("description")) }
    }

    // update views from local variables
    private fun updateView() {
        val tvFullName = findViewById<TextView>(R.id.name)
        val tvNickName = findViewById<TextView>(R.id.nickName)
        val tvBio = findViewById<TextView>(R.id.bio)
        val tvEmail = findViewById<TextView>(R.id.email)
        val tvPhoneNumber = findViewById<TextView>(R.id.phoneNumber)
        val tvLocation = findViewById<TextView>(R.id.location)
        val skillsLayout = findViewById<LinearLayout>(R.id.skills)

        // TODO use placeholder
        tvFullName.text = "$name $surname"
        tvBio.text = bio
        tvNickName.text = nickname
        tvEmail.text = email
        tvPhoneNumber.text = phone
        tvLocation.text = location
        // map active skills to skill cards and add them to the layout
        skills.filter{ s -> s.active}.forEach {s -> skillsLayout.addView(SkillCard(this, s)) }
    }

    // run editProfileActivity
    private fun editProfile() {
        Log.e("edit", "edit profile")
        val i = Intent(this, EditProfileActivity::class.java)

        i.putExtra(getString(R.string.key_name), name)
        i.putExtra(getString(R.string.key_surname), surname)
        i.putExtra(getString(R.string.key_nickname), nickname)
        i.putExtra(getString(R.string.key_bio), bio)
        i.putExtra(getString(R.string.key_email), email)
        i.putExtra(getString(R.string.key_phone_number), phone)
        i.putExtra(getString(R.string.key_location), location)

        // TODO pass skills
        startForResult.launch(i)
    }

    // inflate main_menu into activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu);
        return true
    }

    // pencil context menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        editProfile()
        return super.onOptionsItemSelected(item)
    }
}

