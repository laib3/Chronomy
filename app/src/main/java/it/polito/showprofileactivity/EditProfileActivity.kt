package it.polito.showprofileactivity

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //reference the ImageButton and attach to it the camera_floating_context_menu
        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        registerForContextMenu(imageButton)

        //get all the skills and map them into cards
        val skills_array: Array<String> = resources.getStringArray(R.array.skills_array)
        //TODO: place a loop to map the skills

        val card = findViewById<CardView>(R.id.skill1)
        card.setOnClickListener{
            //inflate the dialog with custom view
            val mDialogView = layoutInflater.inflate(R.layout.skill_edit_modal, null)
            val question = mDialogView.findViewById<TextView>(R.id.question)
            question.setText("Is ${skills_array[0].toString().lowercase()} one of your skills?")
            //AlertDialogBuilder + show
            val mBuilder= AlertDialog.Builder(this)
                .setView(mDialogView)
                //.setTitle("Edit Skill")
            val mAlertDialog = mBuilder.show()
            //dismiss button
            val close_button = mDialogView.findViewById<ImageView>(R.id.close_button)
            close_button.setOnClickListener{
                mAlertDialog.dismiss()
            }

        }
    }

    //tell that the layout we want for this context menu is in camera_floating_context_menu.xml
    //NB: v is the View that was clicked
    override fun onCreateContextMenu(menu: ContextMenu?,v: View?,menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.setHeaderTitle("Change your picture")
        menuInflater.inflate(R.menu.camera_floating_context_menu, menu)
    }

    //functions to manage the selection in the context menu
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.option_1->{
                Toast.makeText(this, "Opening the gallery", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.option_2-> {
                Toast.makeText(this, "Opening the camera", Toast.LENGTH_LONG).show()
                return true
            }
            else-> return super.onContextItemSelected(item)
        }
    }
}