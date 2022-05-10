package it.polito.mainactivity.ui.userprofile.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.databinding.FragmentEditProfileBinding

class EditProfileFragment: Fragment() {
    // fragment which contains EditProfileImageFragment and EditProfileFieldsFragment

    private var _binding: FragmentEditProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    fun notifyMessageEditedProfile() {
        // message to display in the snackbar in the showProfileFragment if the profile was changed
        (activity as MainActivity).snackBarMessage = "Profile edited successfully"
    }

}