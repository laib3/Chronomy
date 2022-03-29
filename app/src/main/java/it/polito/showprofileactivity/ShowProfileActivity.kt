package it.polito.showprofileactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.cardview.widget.CardView

class ShowProfileActivity: AppCompatActivity() {

    private val SHPR_NAME:String = "sharedPreferences"

    private val NICKNAME_KEY:String = "nn"
    private val FULLNAME_KEY:String = "fn"
    private val BIO_KEY:String = "bio"
    private val EMAIL_KEY:String = "email"
    private val PHONE_KEY:String = "phone"
    private val LOCATION_KEY:String = "loc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_profile_activity)

        // save content to shared resources
        saveContent("Mario Rossi",
            "mrossi",
            "Based in Italy",
            "mariorossi@gmail.com",
            "+393001992031",
            "Via RossiMario 13, Italy")
        // load from shared resources */
        loadContent()

        val titles:MutableList<String> = mutableListOf<String>()
        titles.add("titolo1")
        titles.add("titolo2")
        titles.add("titolo3")
        titles.add("titolo4")
        titles.add("titolo5")

        val skills = findViewById<LinearLayout>(R.id.skills)

        titles
            .map {
            title ->
                val textView = TextView(this)
                textView.text = title
                textView
            }
            .map{ tv ->
                val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 200)
                val c = CardView(this)
                c.layoutParams = layoutParams
                c.addView(tv)
                c
            }
            .forEach {c ->
                skills.addView(c)
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

        val tvFullName = findViewById<TextView>(R.id.fullName)
        val tvNickName = findViewById<TextView>(R.id.nickname)
        val tvBio = findViewById<TextView>(R.id.bio)
        val tvEmail = findViewById<TextView>(R.id.email)
        val tvPhoneNumber = findViewById<TextView>(R.id.phoneNumber)
        val tvLocation = findViewById<TextView>(R.id.location)

        tvFullName.text = fullNameText
        tvNickName.text = "@${nickNameText}"
        tvBio.text = bioText
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

    //inflate main_menu into activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = Intent(this, EditProfileActivity::class.java)
        startActivity(i)
        return super.onOptionsItemSelected(item)
    }
}