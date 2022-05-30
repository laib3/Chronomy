package it.polito.mainactivity.ui.userprofile.editprofile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.squareup.picasso.Picasso
import it.polito.mainactivity.databinding.FragmentEditProfilePictureBinding
import it.polito.mainactivity.viewModel.UserProfileViewModel
import java.io.ByteArrayOutputStream
import java.util.*

class EditProfilePictureFragment : Fragment() {

    private val vm: UserProfileViewModel by activityViewModels()

    private var _binding: FragmentEditProfilePictureBinding? = null

    private val storage = FirebaseStorage.getInstance()

    // take picture from camera
    private val takeCameraPicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                if(loadPictureToDb(bitmap)){
                    // change the message for the show profile fragment
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }

    // load picture from gallery
    private val takeGalleryPicture =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val inputStream = activity?.contentResolver?.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                if(loadPictureToDb(bitmap)){
                    // change the message for the show profile fragment
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }
            }
        }

    private fun loadPictureToDb(bitmap: Bitmap): Boolean{
        // upload new picture to db and change the saved url
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val path = "profilePictures/" + UUID.randomUUID() + ".png"
        val profilePictures = storage.getReference(path)

        val metadata =
            StorageMetadata.Builder().setCustomMetadata("uid", vm.user.value!!.userId).build()
        profilePictures.putBytes(data, metadata).addOnSuccessListener {
            profilePictures.downloadUrl.addOnCompleteListener {
                vm.updateUserField(
                    "profilePictureUrl",
                    it.result.toString()
                )
                it.result.toString()
            }
        }
        return true
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
            it?.profilePictureUrl?.apply {
                Picasso.get().load(this).into(binding.profilePictureEditable)
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