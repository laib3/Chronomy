package it.polito.mainactivity.ui.userprofile.editprofile

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.databinding.FragmentEditProfilePictureBinding
import it.polito.mainactivity.ui.userprofile.UserProfileViewModel

class EditProfilePictureFragment : Fragment() {

    private val vm: UserProfileViewModel by activityViewModels()

    private var _binding: FragmentEditProfilePictureBinding? = null

    // take picture from camera
    private val takeCameraPicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                binding.profilePictureEditable.setImageBitmap(bitmap)
                val d: Drawable = BitmapDrawable(resources, bitmap)
                // change the message for the show profile fragment
                (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                //TODO: implement this with new vm
                //vm.setPicture(d)
            }
        }

    // load picture from gallery
    private val takeGalleryPicture =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = activity?.contentResolver?.openInputStream(uri)
                val d: Drawable = Drawable.createFromStream(inputStream, uri.toString())

                // change the message for the show profile fragment
                (parentFragment as EditProfileFragment).notifyMessageEditedProfile()

                //TODO: implement this with new vm
                //vm.setPicture(d)
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
        vm.user.observe(viewLifecycleOwner)
        {
            if (it?.profilePicture != null) {
                //binding.profilePictureEditable.setImageDrawable(it.profilePicture)
                binding.profilePictureEditable.setImageDrawable(it.profilePicture)
            }
        }

        val addPhotoButton = binding.imageAddIcon
        // show dialog for selecting picture from camera or gallery
        addPhotoButton.setOnClickListener { _ ->
            val dialogFragment =
                ProfilePictureChangeDialogFragment(takeCameraPicture, takeGalleryPicture)
            dialogFragment.show(childFragmentManager, "picture")
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}