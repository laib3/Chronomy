package it.polito.showprofileactivity

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //reference the ImageButton and attach to it the camera_floating_context_menu
        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        registerForContextMenu(imageButton)
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