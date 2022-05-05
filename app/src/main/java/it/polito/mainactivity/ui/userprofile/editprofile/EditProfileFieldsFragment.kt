package it.polito.mainactivity.ui.userprofile.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import it.polito.mainactivity.databinding.FragmentEditProfileFieldsBinding
import it.polito.mainactivity.ui.userprofile.SkillCard
import it.polito.mainactivity.ui.userprofile.UserProfileViewModel

class EditProfileFieldsFragment : Fragment() {

    private val vm: UserProfileViewModel by activityViewModels()

    private var _binding: FragmentEditProfileFieldsBinding? = null

    // this property is only valid between onCreateView and
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

        // if updated changes, update the whole list
        vm.updated.observe(viewLifecycleOwner){
            val skills = vm.skills.value
            if(it != null){
                vm.setSkills(skills?.map{ s -> if(it.title != s.title) s else it })
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel(){
        vm.name.observe(viewLifecycleOwner) { binding.textInputEditTextName.setText(it) }
        vm.surname.observe(viewLifecycleOwner) { binding.textInputEditTextSurname.setText(it) }
        vm.nickname.observe(viewLifecycleOwner) { binding.textInputEditTextNickname.setText(it) }
        vm.bio.observe(viewLifecycleOwner) { binding.textInputEditTextBio.setText(it) }
        vm.phone.observe(viewLifecycleOwner) { binding.textInputEditTextPhone.setText(it) }
        vm.email.observe(viewLifecycleOwner) { binding.textInputEditTextEmail.setText(it) }
        vm.location.observe(viewLifecycleOwner) { binding.textInputEditTextLocation.setText(it) }
        vm.skills.observe(viewLifecycleOwner) {
            binding.editableSkillsLayout.removeAllViews()
            it
                .sortedByDescending{ it.active }
                .map{s -> SkillCard(requireContext(), s, vm, true) }
                .forEach{ sc : SkillCard -> binding.editableSkillsLayout.addView(sc) }
        }
    }

    private fun addFocusChangeListeners(){
        binding.textInputEditTextName.setOnFocusChangeListener{_, focused -> if(!focused) vm.setName(binding.textInputEditTextName.text.toString()) }
        binding.textInputEditTextSurname.setOnFocusChangeListener{_, focused -> if(!focused) vm.setSurname(binding.textInputEditTextSurname.text.toString()) }
        binding.textInputEditTextNickname.setOnFocusChangeListener{_, focused -> if(!focused) vm.setNickname(binding.textInputEditTextNickname.text.toString()) }
        binding.textInputEditTextBio.setOnFocusChangeListener{_, focused -> if(!focused) vm.setBio(binding.textInputEditTextBio.text.toString()) }
        binding.textInputEditTextPhone.setOnFocusChangeListener{_, focused -> if(!focused) vm.setPhone(binding.textInputEditTextPhone.text.toString()) }
        binding.textInputEditTextEmail.setOnFocusChangeListener{_, focused -> if(!focused) vm.setEmail(binding.textInputEditTextEmail.text.toString()) }
        binding.textInputEditTextLocation.setOnFocusChangeListener{_, focused -> if(!focused) vm.setLocation(binding.textInputEditTextLocation.text.toString()) }
    }

}