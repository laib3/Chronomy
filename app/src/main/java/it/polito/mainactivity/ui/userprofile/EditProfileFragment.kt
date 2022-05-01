package it.polito.mainactivity.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userProfileViewModel =
            ViewModelProvider(this).get(UserProfileViewModel::class.java)

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val etName = binding.textInputEditTextName
        val etSurname = binding.textInputEditTextSurname
        val etNickname = binding.textInputEditTextNickname
        val etBio = binding.textInputEditTextBio
        val etPhone = binding.textInputEditTextPhone
        val etEmail = binding.textInputEditTextEmail
        val etLocation = binding.textInputEditTextLocation
        userProfileViewModel.name.observe(viewLifecycleOwner) { etName.setText(it) }
        userProfileViewModel.surname.observe(viewLifecycleOwner) { etSurname.setText(it) }
        userProfileViewModel.nickname.observe(viewLifecycleOwner) { etNickname.setText(it) }
        userProfileViewModel.bio.observe(viewLifecycleOwner) { etBio.setText(it) }
        userProfileViewModel.phone.observe(viewLifecycleOwner) { etPhone.setText(it) }
        userProfileViewModel.email.observe(viewLifecycleOwner) { etEmail.setText(it) }
        userProfileViewModel.location.observe(viewLifecycleOwner) { etLocation.setText(it) }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}