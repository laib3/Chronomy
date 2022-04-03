package it.polito.showprofileactivity

import android.content.Context
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class EditSkillCard(c: Context, s: Skill) : CardView(c) {
    init {
        LayoutInflater.from(c).inflate(R.layout.skill_edit_card, this, true)
        val cardTitle = findViewById<TextView>(R.id.skill_name)
        val skillIcon = findViewById<ImageView>(R.id.skill_icon)
        cardTitle.text = s.title

        val skills_array: Array<String> = resources.getStringArray(R.array.skills_array)
        val index = skills_array.indexOf(s.title)
        val icon = resources.getStringArray(R.array.skills_icons)[index]
        val skill_icon_id: Int = resources.getIdentifier(icon, "array", BuildConfig.APPLICATION_ID)

        //FIXME: questa funzione vuole un ID, devo cercare di prendere l'id della stringa che voglio
        skillIcon.setImageResource(skill_icon_id)
    }
}
class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var imageButton: ImageButton
    lateinit var radioGroup: RadioGroup

    lateinit var etEditName: EditText
    lateinit var etEditSurname: EditText
    lateinit var etEditNickname: EditText
    lateinit var etEditBio: EditText
    lateinit var etEditEmail: EditText
    lateinit var etEditPhone: EditText
    lateinit var etEditLocation: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


       etEditName = findViewById(R.id.editName)
       etEditSurname = findViewById(R.id.editSurname)
       etEditNickname = findViewById(R.id.editNickname)
       etEditBio = findViewById(R.id.editBio)
       etEditEmail = findViewById(R.id.editEmail)
       etEditPhone = findViewById(R.id.editPhoneNumber)
       etEditLocation = findViewById(R.id.editLocation)

        //place data received from intent into the correct EditText
        placeData()

        //reference the ImageButton and attach to it the camera_floating_context_menu
        imageButton = findViewById<ImageButton>(R.id.imageButton)
        registerForContextMenu(imageButton)
        imageButton.setOnClickListener { onClick(imageButton) }

        //get all the skills and map them into cards
        val skills_array: List<Skill> = resources.getStringArray(R.array.skills_array).map { it -> Skill(it, it.lowercase().replace(" ", "_")) }
        val selectedSkills = findViewById<GridLayout>(R.id.selectedSkills)
        skills_array.forEach{ s->selectedSkills.addView(EditSkillCard(this, s))}

        val card = findViewById<CardView>(R.id.skill1)
        //val card = findViewById<CardView>(R.id.skillCard)
        card.setOnClickListener {
            //inflate the dialog with custom view
            val mDialogView = layoutInflater.inflate(R.layout.skill_edit_modal, null)
            val question = mDialogView.findViewById<TextView>(R.id.question)
            question.setText("Is ${skills_array[0].toString().lowercase()} one of your skills?")
            //AlertDialogBuilder + show
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Edit Skill")
            val mAlertDialog = mBuilder.show()
            //dismiss button
            val close_button = mDialogView.findViewById<ImageView>(R.id.close_button)
            close_button.setOnClickListener {
                mAlertDialog.dismiss()
            }
            val save_button = mDialogView.findViewById<Button>(R.id.save_button)
            save_button.setOnClickListener {
                radioGroup = mDialogView.findViewById<RadioGroup>(R.id.radioGroup)
                val radioId = radioGroup.checkedRadioButtonId;
                val radioButton = mDialogView.findViewById<RadioButton>(radioId)
                //TODO: actual save
                Toast.makeText(this, "Selected ${radioButton.text}", Toast.LENGTH_LONG).show()
                mAlertDialog.dismiss()
            }
        }
    }

    override fun onClick(v: View?) {
        openContextMenu(v)
    }

    //tell that the layout we want for this context menu is in camera_floating_context_menu.xml
    //NB: v is the View that was clicked
    override fun onCreateContextMenu(menu: ContextMenu?,v: View?,menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.setHeaderTitle("Change your picture")
        menuInflater.inflate(R.menu.camera_floating_context_menu, menu)
    }

    //functions to manage the selection in the popup menu
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_1 -> {
                Toast.makeText(this, "Opening the gallery", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.option_2 -> {
                Toast.makeText(this, "Opening the camera", Toast.LENGTH_LONG).show()
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }

    private fun placeData(){
        // receive data from showProfileActivity
        val nickName: String = intent.getStringExtra(getString(R.string.key_nickname)) ?: ""
        val name:String = intent.getStringExtra(getString(R.string.key_name))?:""
        val surname:String = intent.getStringExtra(getString(R.string.key_surname))?:""
        val email: String = intent.getStringExtra(getString(R.string.key_email)) ?: ""
        val phone: String = intent.getStringExtra(getString(R.string.key_phone_number)) ?: ""
        val bio: String = intent.getStringExtra(getString(R.string.key_bio)) ?: ""
        val location: String = intent.getStringExtra(getString(R.string.key_location)) ?: ""

        etEditName.setText(name)
        etEditSurname.setText(surname)
        etEditNickname.setText(nickName)
        etEditEmail.setText(email)
        etEditPhone.setText(phone)
        etEditBio.setText(bio)
        etEditLocation.setText(location)
    }
}