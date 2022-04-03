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

    // TODO remove

    fun createSkills () : List<Skill>{
        // return a list of skills that contains all the titles present in skill.xml file
        // src of a skill is the name with spaces replaced with underscore and lowercase
        val skills:MutableList<Skill> = mutableListOf<Skill>()
        resources.getStringArray(R.array.skills_array).forEach{ s -> skills.add(Skill(s, s.lowercase().replace(" ", "_"))) }
        return skills
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_profile_activity)

        val skills = createSkills()

        // just static adding for testing TODO: REMOVE
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
        // TODO: REMOVE

        var tvFullName = findViewById<TextView>(R.id.name)
        var tvNickName = findViewById<TextView>(R.id.nickName)
        var tvBio = findViewById<TextView>(R.id.bio)
        var tvEmail = findViewById<TextView>(R.id.email)
        var tvPhoneNumber = findViewById<TextView>(R.id.phoneNumber)
        var tvLocation = findViewById<TextView>(R.id.location)

        // save content to shared resources
        saveContent("Mario",
            "Rossi",
            "rossino",
            "Based in Italy, I love animals, simpatico, solare, in cerca di amicizie.",
            "mariorossi@gmail.com",
            "+393001992031",
            "Via RossiMario 13, Italy",
            skills)
        // load from shared resources */
        loadContent()

        val iv = findViewById<ImageView>(R.id.profilePicture)
        iv.clipToOutline = true

        val skillsLayout = findViewById<LinearLayout>(R.id.skills)
        // map active skills to skill cards and add them to the layout
        skills.filter{ s -> s.active}.forEach {s -> skillsLayout.addView(SkillCard(this, s)) }

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
                // TODO save content
                saveContent(name, surname, nickname, bio, email, phone, location, skills)
                // TODO load content
            }
        }


    }

    // content must be loaded from a single JSON
    fun loadContent(){
        val sharedPreferences = getSharedPreferences(SHPR_NAME, MODE_PRIVATE)
        val profile:String = sharedPreferences.getString(PROFILE, "profile") ?: "profile"
        val jobj = JSONObject(profile)

        val tvFullName = findViewById<TextView>(R.id.name)
        val tvNickName = findViewById<TextView>(R.id.nickName)
        val tvBio = findViewById<TextView>(R.id.bio)
        val tvEmail = findViewById<TextView>(R.id.email)
        val tvPhoneNumber = findViewById<TextView>(R.id.phoneNumber)
        val tvLocation = findViewById<TextView>(R.id.location)

        tvFullName.text = jobj.getString("name") + " " + jobj.getString("surname")
        tvBio.text = jobj.getString("bio")
        tvNickName.text = jobj.getString("nickName")
        tvEmail.text = jobj.getString("email")
        tvPhoneNumber.text = jobj.getString("phone")
        tvLocation.text = jobj.getString("location")
    }

    fun saveContent(name:String, surname:String, nickName:String, bio:String, email:String, phone:String, location:String, skills:List<Skill>){
        val sharedPreferences = getSharedPreferences(SHPR_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // put a profile string in the shared preferences
        editor.putString(PROFILE, """{ "name": "$name", "surname":"$surname", "nickname":"$nickName", "bio":"$bio", "email":"$email", "phone":"$phone", "location":"$location" }""".trimMargin())
        // save
        editor.apply()
    }

    private fun editProfile() {
        Log.e("edit", "edit profile")
        val i = Intent(this, EditProfileActivity::class.java)
        val tvFullName = findViewById<TextView>(R.id.name)
        val tvNickName = findViewById<TextView>(R.id.nickName)
        val tvBio = findViewById<TextView>(R.id.bio)
        val tvEmail = findViewById<TextView>(R.id.email)
        val tvPhoneNumber = findViewById<TextView>(R.id.phoneNumber)
        val tvLocation = findViewById<TextView>(R.id.location)

        i.putExtra(getString(R.string.key_full_name), tvFullName.text)
        i.putExtra(getString(R.string.key_nickname), tvNickName.text)
        i.putExtra(getString(R.string.key_bio), tvBio.text)
        i.putExtra(getString(R.string.key_email),tvEmail.text )
        i.putExtra(getString(R.string.key_phone_number),tvPhoneNumber.text )
        i.putExtra(getString(R.string.key_location), tvLocation.text )

        startForResult.launch(i)
    }

    //inflate main_menu into activity
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