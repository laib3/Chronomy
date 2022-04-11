package it.polito.showprofileactivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    private val sharedPrefName:String = "sharedPreferences"
    private val profile:String = "profile"

    private lateinit var name:String
    private lateinit var surname:String
    private lateinit var nickname:String
    private lateinit var bio:String
    private lateinit var email:String
    private lateinit var phone:String
    private lateinit var location:String
    private lateinit var skills:List<Skill>
    private var currentPhotoPath:String = ""

    private lateinit var startForResult : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_profile_activity)

        supportActionBar?.title = "Profile"

        // load content from shared preferences
        loadContent()
        updateView()

        // rounded profile picture
        val iv = findViewById<ImageView>(R.id.profilePicture)
        iv.clipToOutline = true

        // use registerForActivityResult in place of startActivityForResult because the latter is deprecated
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val i = result.data
                // Handle the Intent
                name = i?.getStringExtra(getString(R.string.key_name)) ?: ""
                surname = i?.getStringExtra(getString(R.string.key_surname)) ?: ""
                nickname = i?.getStringExtra(getString(R.string.key_nickname)) ?: ""
                bio = i?.getStringExtra(getString(R.string.key_bio)) ?: ""
                email = i?.getStringExtra(getString(R.string.key_email)) ?: ""
                phone = i?.getStringExtra(getString(R.string.key_phone_number)) ?: ""
                location = i?.getStringExtra(getString(R.string.key_location)) ?: ""
                currentPhotoPath = i?.getStringExtra(getString(R.string.key_currentPhotoPath)) ?: ""
                skills = jsonToSkills(JSONArray(i?.getStringExtra(getString(R.string.key_skills)) ?: ""))

                // save content to shared preferences
                saveContent()
                updateView()
            }
        }
    }

    // save content to shared memory
    private fun saveContent(){
        val sharedPreferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // put a profile string in the shared preferences
        val jsonSkills:String = skillsToJsonString(skills)

        val profileString: String =
            """{ "name": "$name", "surname":"$surname", "nickname":"$nickname", "bio":"$bio", "email":"$email", "phone":"$phone", "location":"$location", "photo":"$currentPhotoPath", "skills":$jsonSkills }""".trimIndent()
        editor.putString(profile, profileString)
        // save
        editor.apply()
    }

    // load content and update local variables
    private fun loadContent(){
        val sharedPreferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE)
        val profile:String? = sharedPreferences.getString(profile, null)
        val jsonObject:JSONObject? = if(profile == null){
            null
        } else {
            JSONObject(profile)
        }
        name = jsonObject?.getString("name") ?: getString(R.string.name)
        surname = jsonObject?.getString("surname") ?: getString(R.string.surname)
        nickname = jsonObject?.getString("nickname") ?: getString(R.string.nickname)
        bio = jsonObject?.getString("bio") ?: getString(R.string.bio)
        email = jsonObject?.getString("email") ?: getString(R.string.email)
        phone = jsonObject?.getString("phone") ?: getString(R.string.phone_number)
        location = jsonObject?.getString("location") ?: getString(R.string.location)
        currentPhotoPath = jsonObject?.getString("photo") ?: "" // warning: it may be null

        // if skills are found in memory load them from memory, otherwise create them from scratch
        skills = if(jsonObject == null)
            createSkills(this)
        else
            jsonToSkills(jsonObject.getJSONArray("skills"))
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
        val ivProfilePicture = findViewById<ImageView>(R.id.profilePicture)

        tvFullName.text = String.format(getString(R.string.fullname_placeholder), name, surname)
        tvBio.text = String.format(getString(R.string.bio_placeholder), bio)
        tvNickName.text = String.format(getString(R.string.nickname_placeholder), nickname)
        tvEmail.text = email
        tvPhoneNumber.text = phone
        tvLocation.text = location
        // clean layout
        skillsLayout.removeAllViews()
        // map active skills to skill cards and add them to the layout
        skills.filter{ s -> s.active}.forEach { s -> skillsLayout.addView(SkillCard(this, s)) }
        if(currentPhotoPath != "")
            ivProfilePicture.setImageURI(Uri.parse(currentPhotoPath))
    }

    // run editProfileActivity
    private fun editProfile() {
        val i = Intent(this, EditProfileActivity::class.java)

        i.putExtra(getString(R.string.key_name), name)
        i.putExtra(getString(R.string.key_surname), surname)
        i.putExtra(getString(R.string.key_nickname), nickname)
        i.putExtra(getString(R.string.key_bio), bio)
        i.putExtra(getString(R.string.key_email), email)
        i.putExtra(getString(R.string.key_phone_number), phone)
        i.putExtra(getString(R.string.key_location), location)
        i.putExtra(getString(R.string.key_skills), skillsToJsonString(skills))
        i.putExtra(getString(R.string.key_currentPhotoPath), currentPhotoPath)

        Log.e("edit", "edit profile")
        startForResult.launch(i)
    }

    // inflate main_menu into activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // pencil context menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        editProfile()
        return super.onOptionsItemSelected(item)
    }
}

