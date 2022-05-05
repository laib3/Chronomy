package it.polito.mainactivity.ui.userprofile.editprofile

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.DialogFragment

class ProfilePictureChangeDialogFragment(
    val cameraAction: ActivityResultLauncher<Void>,
    val galleryAction: ActivityResultLauncher<String>): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val options = arrayOf("Camera", "Gallery")
            builder.setTitle("Select picture from")
                .setItems(options) { _, which ->
                    // camera
                    if (which == 0) {
                        cameraAction.launch(null)
                    }
                    // gallery
                    else {
                        galleryAction.launch("image/*")
                    }
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}