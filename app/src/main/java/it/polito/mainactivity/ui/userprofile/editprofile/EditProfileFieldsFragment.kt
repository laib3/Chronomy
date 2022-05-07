package it.polito.mainactivity.ui.userprofile.editprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.databinding.FragmentEditProfileFieldsBinding
import it.polito.mainactivity.model.Skill
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
        vm.updated.observe(viewLifecycleOwner) {
            val skills = vm.skills.value
            if (it != null) {
                Log.d("DBG_updated","Updated!")
                vm.setSkills(skills?.map { s -> if (it.title != s.title) s else it })
                (activity as MainActivity)?.setFragmentTransactionMessage("Profile edited")
                // reset value of updated to null
                vm.resetUpdated()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
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
                .sortedByDescending { it.active }
                .map { s -> SkillCard(requireContext(), s, vm, true) }
                .forEach { sc: SkillCard -> binding.editableSkillsLayout.addView(sc) }
        }
    }

    private fun addFocusChangeListeners() {
        binding.textInputEditTextName.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.name.value.toString() != binding.textInputEditTextName.text.toString()) {
                    vm.setName(binding.textInputEditTextName.text.toString())
                    (activity as MainActivity)?.setFragmentTransactionMessage("Profile edited")
                }

            }
        }
        binding.textInputEditTextSurname.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.surname.value.toString() != binding.textInputEditTextSurname.text.toString()) {
                    vm.setSurname(binding.textInputEditTextSurname.text.toString())
                    (activity as MainActivity)?.setFragmentTransactionMessage("Profile edited")
                }

            }
        }
        binding.textInputEditTextNickname.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.nickname.value.toString() != binding.textInputEditTextNickname.text.toString()) {
                    vm.setNickname(binding.textInputEditTextNickname.text.toString())
                    (activity as MainActivity)?.setFragmentTransactionMessage("Profile edited")
                }

            }
        }
        binding.textInputEditTextBio.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.bio.value.toString() != binding.textInputEditTextBio.text.toString()) {
                    vm.setBio(binding.textInputEditTextBio.text.toString())
                    (activity as MainActivity)?.setFragmentTransactionMessage("Profile edited")
                }

            }
        }
        binding.textInputEditTextPhone.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.phone.value.toString() != binding.textInputEditTextPhone.text.toString()) {
                    vm.setPhone(binding.textInputEditTextPhone.text.toString())
                    (activity as MainActivity)?.setFragmentTransactionMessage("Profile edited")
                }

            }
        }
        binding.textInputEditTextEmail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.email.value.toString() != binding.textInputEditTextEmail.text.toString()) {
                    vm.setEmail(binding.textInputEditTextEmail.text.toString())
                    (activity as MainActivity)?.setFragmentTransactionMessage("Profile edited")
                }

            }
        }
        binding.textInputEditTextLocation.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.location.value.toString() != binding.textInputEditTextLocation.text.toString()) {
                    vm.setLocation(binding.textInputEditTextLocation.text.toString())
                    (activity as MainActivity)?.setFragmentTransactionMessage("Profile edited")
                }

            }
        }
    }

}