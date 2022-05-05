package it.polito.mainactivity.ui.userprofile.editprofile

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import it.polito.mainactivity.databinding.FragmentEditProfilePictureBinding
import it.polito.mainactivity.ui.userprofile.ProfilePictureChangeDialogFragment
import it.polito.mainactivity.ui.userprofile.UserProfileViewModel
import java.nio.file.Path

class EditProfilePictureFragment: Fragment() {

    private val userProfileViewModel: UserProfileViewModel by activityViewModels()

    private var _binding: FragmentEditProfilePictureBinding? = null

    private val takeCameraPicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){ bitmap ->
        if(bitmap != null){
            binding.profilePictureEditable.setImageBitmap(bitmap)
            val d: Drawable = BitmapDrawable(resources, bitmap)
            userProfileViewModel.setPicture(d)
        }
    }
    private val takeGalleryPicture = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri != null) {
            val inputStream = activity?.contentResolver?.openInputStream(uri)
            val d: Drawable = Drawable.createFromStream(inputStream, uri.toString())
            userProfileViewModel.setPicture(d)
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditProfilePictureBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.profilePictureEditable.clipToOutline = true

        /* if the profile picture changes set it inside the imageview */
        userProfileViewModel.picture.observe(viewLifecycleOwner)
            { if(it != null) binding.profilePictureEditable.setImageDrawable(it) }

        val addPhotoButton = binding.imageAddIcon
        // show dialog for selecting picture from camera or gallery
        addPhotoButton.setOnClickListener { _ ->
            val dialogFragment = ProfilePictureChangeDialogFragment(takeCameraPicture, takeGalleryPicture)
            dialogFragment.show(childFragmentManager, "picture")
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}