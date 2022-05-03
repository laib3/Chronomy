package it.polito.mainactivity.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import it.polito.mainactivity.databinding.FragmentEditProfileFieldsBinding

class EditProfileFieldsFragment : Fragment() {

    private val userProfileViewModel:UserProfileViewModel by activityViewModels()

    private var _binding: FragmentEditProfileFieldsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditProfileFieldsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        observeViewModel()
        addFocusChangeListeners()
        val skills = userProfileViewModel.skills
        // TODO make it more efficient (somehow - do not remove skills and add them)
        skills.forEach{ s -> s.active.observe(viewLifecycleOwner){ onSkillActiveChange() } }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel(){
        userProfileViewModel.name.observe(viewLifecycleOwner) { binding.textInputEditTextName.setText(it) }
        userProfileViewModel.surname.observe(viewLifecycleOwner) { binding.textInputEditTextSurname.setText(it) }
        userProfileViewModel.nickname.observe(viewLifecycleOwner) { binding.textInputEditTextNickname.setText(it) }
        userProfileViewModel.bio.observe(viewLifecycleOwner) { binding.textInputEditTextBio.setText(it) }
        userProfileViewModel.phone.observe(viewLifecycleOwner) { binding.textInputEditTextPhone.setText(it) }
        userProfileViewModel.email.observe(viewLifecycleOwner) { binding.textInputEditTextEmail.setText(it) }
        userProfileViewModel.location.observe(viewLifecycleOwner) { binding.textInputEditTextLocation.setText(it) }
    }

    private fun addFocusChangeListeners(){
        binding.textInputEditTextName.setOnFocusChangeListener{_, focused -> if(!focused) userProfileViewModel.setName(binding.textInputEditTextName.text.toString()) }
        binding.textInputEditTextSurname.setOnFocusChangeListener{_, focused -> if(!focused) userProfileViewModel.setSurname(binding.textInputEditTextSurname.text.toString()) }
        binding.textInputEditTextNickname.setOnFocusChangeListener{_, focused -> if(!focused) userProfileViewModel.setNickname(binding.textInputEditTextNickname.text.toString()) }
        binding.textInputEditTextBio.setOnFocusChangeListener{_, focused -> if(!focused) userProfileViewModel.setBio(binding.textInputEditTextBio.text.toString()) }
        binding.textInputEditTextPhone.setOnFocusChangeListener{_, focused -> if(!focused) userProfileViewModel.setPhone(binding.textInputEditTextPhone.text.toString()) }
        binding.textInputEditTextEmail.setOnFocusChangeListener{_, focused -> if(!focused) userProfileViewModel.setEmail(binding.textInputEditTextEmail.text.toString()) }
        binding.textInputEditTextLocation.setOnFocusChangeListener{_, focused -> if(!focused) userProfileViewModel.setLocation(binding.textInputEditTextLocation.text.toString()) }
    }

    private fun onSkillActiveChange(){
        val skillsLayout = binding.editableSkillsLayout
        skillsLayout.removeAllViews()
        val skills = userProfileViewModel.skills.sortedByDescending { it.active.value }
        skills
            .map{ s -> SkillCardEditable(requireContext(), this, s) }
            .forEach{ sc -> skillsLayout.addView(sc) }
    }

}