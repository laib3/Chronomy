package it.polito.showprofileactivity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
// import android.icu.text.SimpleDateFormat
import java.util.*

//intent return values
private const val REQUEST_IMAGE_CAPTURE = 1
private const val REQUEST_IMAGE_FROM_GALLERY = 2

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var imageButton: ImageButton //opens the context menu to choose between camera or gallery

    //all the edit text that needs to be filled with data from intent
    private lateinit var etEditName: EditText
    private lateinit var etEditSurname: EditText
    private lateinit var etEditNickname: EditText
    private lateinit var etEditBio: EditText
    private lateinit var etEditEmail: EditText
    private lateinit var etEditPhone: EditText
    private lateinit var etEditLocation: EditText

    //the layouts that needs to be filled with the skills of the user
    private lateinit var selectedSkills: LinearLayout
    private lateinit var notSelectedSkills: LinearLayout

    //list in which are saved all the skills
    private lateinit var skills: List<Skill>

    //where the profile picture is shown
    private lateinit var ivEditProfilePic: ImageView


    //URI for the current profile picture
    private var imageUri: Uri? = null
    //path of the current profile picture
    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile_activity)

        supportActionBar?.title = "Edit Profile"

        etEditName = findViewById(R.id.editName)
        etEditSurname = findViewById(R.id.editSurname)
        etEditNickname = findViewById(R.id.editNickname)
        etEditBio = findViewById(R.id.editBio)
        etEditEmail = findViewById(R.id.editEmail)
        etEditPhone = findViewById(R.id.editPhoneNumber)
        etEditLocation = findViewById(R.id.editLocation)
        ivEditProfilePic = findViewById(R.id.profilePicture)
        ivEditProfilePic.clipToOutline = true

        selectedSkills = findViewById(R.id.selectedSkills)
        notSelectedSkills = findViewById(R.id.notSelectedSkills)

        /* display the maximum input length in the helper - pick it from constraints.xml */
        val tvHelper = findViewById<TextView>(R.id.helper)
        tvHelper.text = String.format(getString(R.string.bio_helper), this.resources.getInteger(R.integer.maxInputLength))

        //place data received from INTENT into the correct EditText
        placeData()

        //reference the ImageButton and attach to it the camera_floating_context_menu
        imageButton = findViewById(R.id.imageButton)
        registerForContextMenu(imageButton)
        imageButton.setOnClickListener { onClick(imageButton) }

        //get all the skills and map them into cards
        refreshSkills()

        //load saved instance
        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString("state_currentPhotoPath")
            if(currentPhotoPath != null) {
                ivEditProfilePic.setImageURI(Uri.parse(currentPhotoPath))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("state_currentPhotoPath", currentPhotoPath)
    }

    private fun placeData() {
        // receive data from showProfileActivity
        val nickName: String = intent.getStringExtra(getString(R.string.key_nickname)) ?: ""
        val name: String = intent.getStringExtra(getString(R.string.key_name)) ?: ""
        val surname: String = intent.getStringExtra(getString(R.string.key_surname)) ?: ""
        val email: String = intent.getStringExtra(getString(R.string.key_email)) ?: ""
        val phone: String = intent.getStringExtra(getString(R.string.key_phone_number)) ?: ""
        val bio: String = intent.getStringExtra(getString(R.string.key_bio)) ?: ""
        val location: String = intent.getStringExtra(getString(R.string.key_location)) ?: ""
        currentPhotoPath = intent.getStringExtra(getString(R.string.key_currentPhotoPath))

        etEditName.setText(name)
        etEditSurname.setText(surname)
        etEditNickname.setText(nickName)
        etEditEmail.setText(email)
        etEditPhone.setText(phone)
        etEditBio.setText(bio)
        etEditLocation.setText(location)
        skills = jsonToSkills(JSONArray( intent.getStringExtra(getString(R.string.key_skills)) ?: ""))
        if(currentPhotoPath != null)
            ivEditProfilePic.setImageURI(Uri.parse(currentPhotoPath))
    }

    private fun refreshSkills() {
        //remove the existing children
        selectedSkills.removeAllViews()
        notSelectedSkills.removeAllViews()
        //add the new children
        //separation of the active/inactive skills, then add a card for each skill
        skills.filter { s -> s.active }.forEach { s -> populate(selectedSkills, s) }
        skills.filter { s -> !s.active }.forEach { s -> populate(notSelectedSkills, s) }
    }

    private fun populate(l: LinearLayout, s: Skill) {
        val card = SkillCard(this, s)
        card.setOnClickListener {
            //inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.skill_edit_modal, null)

            val title = mDialogView.findViewById<TextView>(R.id.modalTitle)
            title.text = String.format(getString(R.string.edit_skill_dialog_text), s.title)
            val question = mDialogView.findViewById<TextView>(R.id.question)
            question.text = String.format(getString(R.string.edit_skill_dialog_question), s.title.lowercase())

            //set the radio state starting from skill.active
            val radioGroup = mDialogView.findViewById<RadioGroup>(R.id.radioGroup)
            val radioButtonYES = radioGroup.findViewById<RadioButton>(R.id.radioButtonYES)
            val radioButtonNO = radioGroup.findViewById<RadioButton>(R.id.radioButtonNO)
            radioButtonYES.isChecked = s.active
            radioButtonNO.isChecked = !s.active

            //populate the description
            val description = mDialogView.findViewById<EditText>(R.id.editDescription)
            description.setText(s.description)

            //Creation and showing the modal
            // set description hint max char length
            mDialogView.findViewById<TextView>(R.id.description_hint).text =
                String.format(getString(R.string.skill_description_helper),
                this.resources.getInteger(R.integer.maxInputLength))

            //AlertDialogBuilder + show
            val mBuilder = android.app.AlertDialog.Builder(this)
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()

            //Attaching the corresponding functions to close and save buttons
            val closeButton = mDialogView.findViewById<ImageView>(R.id.close_button)
            closeButton.setOnClickListener {
                mAlertDialog.dismiss()
            }

            val saveButton = mDialogView.findViewById<Button>(R.id.save_button)
            saveButton.setOnClickListener {
                //get the current state of radioButtonYES. then update the skill's status and description
                s.active = radioButtonYES.isChecked
                s.description = description.text.toString()
                val desc = card.findViewById<TextView>(R.id.skillDescription)
                desc.text = s.description
                //update skills
                refreshSkills()
                mAlertDialog.dismiss() //close dialog
            }
        }
        l.addView(card) //add skill card into layout
    }

    override fun onClick(v: View?) {
        openContextMenu(v)
    }

   override fun onCreateContextMenu(menu: ContextMenu?,v: View?,menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.setHeaderTitle("Change your picture")
        menuInflater.inflate(R.menu.camera_floating_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
    //functions to manage the selection in the popup menu
        return when (item.itemId) {
            R.id.option_1 -> {
                chooseFromGallery()
                Toast.makeText(this, "Opening the gallery", Toast.LENGTH_LONG).show()
                true
            }
            R.id.option_2 -> {
                Toast.makeText(this, "Opening the camera", Toast.LENGTH_LONG).show()
                takePhoto()
                true

            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val i = Intent()
        //decorating the intent to pass data
        i.putExtra(getString(R.string.key_name), etEditName.text.toString())
        i.putExtra(getString(R.string.key_surname), etEditSurname.text.toString())
        i.putExtra(getString(R.string.key_nickname), etEditNickname.text.toString())
        i.putExtra(getString(R.string.key_bio), etEditBio.text.toString())
        i.putExtra(getString(R.string.key_email), etEditEmail.text.toString())
        i.putExtra(getString(R.string.key_phone_number), etEditPhone.text.toString())
        i.putExtra(getString(R.string.key_location), etEditLocation.text.toString())
        i.putExtra(getString(R.string.key_skills), skillsToJsonString(skills))
        i.putExtra(getString(R.string.key_currentPhotoPath), currentPhotoPath)
        setResult(Activity.RESULT_OK, i)

        super.onBackPressed()
    }

    // taking profile pic from camera
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            intent.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e("photoFile", "Error creating file")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoUri: Uri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        it
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    //launch camera
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Toast.makeText(this, "It was impossible to open the camera", Toast.LENGTH_LONG).show()
        }
    }

    private fun chooseFromGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        //start gallery
        startActivityForResult(gallery, REQUEST_IMAGE_FROM_GALLERY)
    }

    //save picture into local data
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss",Locale.ITALY).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun updatePathFromGallery(imageUri: Uri?) {
        //retrieve from URI the image path
        val inputStream = this.contentResolver.openInputStream(imageUri!!)
        val cursor = this.contentResolver.query(imageUri, null, null, null, null)
        cursor?.use { c ->
            val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (c.moveToFirst()) {
                val name = c.getString(nameIndex)
                inputStream?.let { inputStream ->
                    // create same file with same name
                    val file = File(this.cacheDir, name)
                    val os = file.outputStream()
                    os.use {
                        inputStream.copyTo(it)
                    }
                    currentPhotoPath = file.absolutePath
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //opening the camera was successful
            ivEditProfilePic.setImageURI(Uri.parse(currentPhotoPath))
        }

        if (requestCode == REQUEST_IMAGE_FROM_GALLERY && resultCode == RESULT_OK) {
            //opening the gallery was successful
            imageUri = data?.data
            updatePathFromGallery(imageUri)
            ivEditProfilePic.setImageURI(imageUri)
        }
    }
}