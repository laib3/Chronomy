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

class EditProfileActivity : AppCompatActivity(), View.OnClickListener{
    lateinit var imageButton: ImageButton
    lateinit var radioGroup: RadioGroup

    lateinit var etEditName: EditText
    lateinit var etEditSurname: EditText
    lateinit var etEditNickname: EditText
    lateinit var etEditBio: EditText
    lateinit var etEditEmail: EditText
    lateinit var etEditPhone: EditText
    lateinit var etEditLocation: EditText

    lateinit var selectedSkills :LinearLayout
    lateinit var notSelectedSkills : LinearLayout

    lateinit var skills : List<Skill>

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

        skills = createSkills(this)

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
            this?.description ="I can bring pizza to your house and have a chat with you!"
        }
        // TODO: REMOVE

        //get all the skills and map them into cards
        refreshSkills()

    }

    private fun refreshSkills(){
        selectedSkills = findViewById<LinearLayout>(R.id.selectedSkills)
        notSelectedSkills = findViewById<LinearLayout>(R.id.notSelectedSkills)
        selectedSkills.removeAllViews()
        notSelectedSkills.removeAllViews()
        skills.filter{ s -> s.active}.forEach {s -> populate(selectedSkills, s)}
        skills.filter{ s -> !s.active}.forEach  {s -> populate(notSelectedSkills, s)}

    }

    private fun populate (l:LinearLayout, s:Skill){
        val card = SkillCard(this, s)
        card.setOnClickListener {
            //inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.skill_edit_modal, null)

            val title = mDialogView.findViewById<TextView>(R.id.modalTitle)
            title.text = "Edit ${s.title}"
            val question = mDialogView.findViewById<TextView>(R.id.question)
            question.text = "Is ${s.title.toString().lowercase()} one of your skills?"

            val radioGroup = mDialogView.findViewById<RadioGroup>(R.id.radioGroup)
            val radioButtonYES = radioGroup.findViewById<RadioButton>(R.id.radioButtonYES)
            val radioButtonNO = radioGroup.findViewById<RadioButton>(R.id.radioButtonNO)
            radioButtonYES.isChecked = s.active
            radioButtonNO.isChecked = !s.active

            val description = mDialogView.findViewById<EditText>(R.id.editDescription)
            description.setText(s.description)

            //AlertDialogBuilder + show
            val mBuilder = android.app.AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Edit Skill")
            val mAlertDialog = mBuilder.show()
            //dismiss button
            val closeButton = mDialogView.findViewById<ImageView>(R.id.close_button)
            closeButton.setOnClickListener {
                mAlertDialog.dismiss()
            }
            val saveButton = mDialogView.findViewById<Button>(R.id.save_button)
            saveButton.setOnClickListener {
                val radioId = radioGroup.checkedRadioButtonId;
                val radioButton = mDialogView.findViewById<RadioButton>(radioId)
                //TODO: actual save
                s.active = radioButtonYES.isChecked
                s.description = description.text.toString()
                val desc = card.findViewById<TextView>(R.id.skillDescription)
                desc.text = s.description
                refreshSkills()
                Toast.makeText(this, "Selected ${radioButton.text}", Toast.LENGTH_LONG).show()
                mAlertDialog.dismiss()
            }
        }
        l.addView(card)
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