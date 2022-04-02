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
import androidx.cardview.widget.CardView



class SkillCard(c: Context, s:Skill): CardView(c){
    init {
        LayoutInflater.from(c).inflate(R.layout.skill_card, this, true)
        val cardTitle = findViewById<TextView>(R.id.skillTitle)
        cardTitle.text = s.title
        val desc = findViewById<TextView>(R.id.skillDescription)
        desc.text = s.description
    }
}

class ShowProfileActivity: AppCompatActivity() {

    /*
    lateinit var tvFullName: TextView
    lateinit var tvNickName: TextView
    lateinit var tvBio: TextView
    lateinit var tvEmail: TextView
    lateinit var tvPhoneNumber: TextView
    lateinit var tvLocation: TextView
    */

    private val SHPR_NAME:String = "sharedPreferences"

    private val NICKNAME_KEY:String = "group02.lab1.nickname"
    private val FULLNAME_KEY:String = "group02.lab1.fullname"
    private val BIO_KEY:String = "group02.lab1.bio"
    private val EMAIL_KEY:String = "group02.lab1.email"
    private val PHONE_KEY:String = "group02.lab1.phone"
    private val LOCATION_KEY:String = "group02.lab1.location"

    private lateinit var startForResult : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_profile_activity)



        /*
        tvFullName = findViewById(R.id.name)
        tvNickName = findViewById(R.id.nickName)
        tvBio = findViewById(R.id.bio)
        tvEmail = findViewById(R.id.email)
        tvPhoneNumber = findViewById(R.id.phoneNumber)
        tvLocation = findViewById(R.id.location)
        */

        // save content to shared resources
        saveContent("Mario Rossi",
            "mrossi",
            "Based in Italy, I love animals, simpatico, solare, in cerca di amicizie.",
            "mariorossi@gmail.com",
            "+393001992031",
            "Via RossiMario 13, Italy")
        // load from shared resources */
        loadContent()

        val skills:MutableList<Skill> = mutableListOf<Skill>()
        skills.add(Skill("Gardening", "I can mow the lawn, trim bushes, rake and pick up leaves in the garden. I can also take care of watering the flowers and plants and putting fertilizer"))
        skills.add(Skill("Home Repair", "I can fix your home appliance"))
        skills.add(Skill("Child Care", "Babysit your kids"))
        skills.add(Skill("Transportation", ""))

        val iv = findViewById<ImageView>(R.id.profilePicture)
        iv.clipToOutline = true

        val skillsLayout = findViewById<LinearLayout>(R.id.skills)
        // map skills to skill cards and add them to the layout
        skills.forEach {s -> skillsLayout.addView(SkillCard(this, s)) }

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                // Handle the Intent

            }
        }


    }

    fun loadContent(){
        val sharedPreferences = getSharedPreferences(SHPR_NAME, MODE_PRIVATE)
        val fullNameText:String = sharedPreferences.getString(FULLNAME_KEY, "Full Name") ?: "Full Name"
        val nickNameText:String = sharedPreferences.getString(NICKNAME_KEY, "NickName") ?: "NickName"
        val bioText:String = sharedPreferences.getString(BIO_KEY, "bio") ?: "bio"
        val emailText:String = sharedPreferences.getString(EMAIL_KEY, "email") ?: "email"
        val phoneNumberText:String = sharedPreferences.getString(PHONE_KEY, "phone") ?: "phone"
        val locationText:String = sharedPreferences.getString(LOCATION_KEY, "location") ?: "location"

        val tvFullName = findViewById<TextView>(R.id.name)
        val tvNickName = findViewById<TextView>(R.id.nickName)
        val tvBio = findViewById<TextView>(R.id.bio)
        val tvEmail = findViewById<TextView>(R.id.email)
        val tvPhoneNumber = findViewById<TextView>(R.id.phoneNumber)
        val tvLocation = findViewById<TextView>(R.id.location)

        tvFullName.text = fullNameText
        tvBio.text = bioText
        tvNickName.text = nickNameText
        tvEmail.text = emailText
        tvPhoneNumber.text = phoneNumberText
        tvLocation.text = locationText
    }

    fun saveContent(fullName:String, nickName:String, bio:String, email:String, phone:String, location:String){
        val sharedPreferences = getSharedPreferences(SHPR_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(FULLNAME_KEY, fullName)
        editor.putString(NICKNAME_KEY, nickName)
        editor.putString(BIO_KEY, bio)
        editor.putString(EMAIL_KEY, email)
        editor.putString(PHONE_KEY, phone)
        editor.putString(LOCATION_KEY, location)

        // save to file
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