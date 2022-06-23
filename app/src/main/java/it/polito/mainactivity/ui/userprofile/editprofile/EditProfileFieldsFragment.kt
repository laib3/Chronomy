package it.polito.mainactivity.ui.userprofile.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import it.polito.mainactivity.databinding.FragmentEditProfileFieldsBinding
import it.polito.mainactivity.ui.userprofile.SkillCard
import it.polito.mainactivity.viewModel.UserProfileViewModel

class EditProfileFieldsFragment : Fragment() {
    private val vm: UserProfileViewModel by activityViewModels()
    private var _binding: FragmentEditProfileFieldsBinding? = null
    private val binding get() = _binding!!
    val editTextChildren = mutableListOf<EditText>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditProfileFieldsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        addFocusChangeListeners()

        editTextChildren.addAll(
            listOf(
                binding.textInputEditTextName,
                binding.textInputEditTextSurname,
                binding.textInputEditTextNickname,
                binding.textInputEditTextBio,
                binding.textInputEditTextPhone,
                binding.textInputEditTextEmail,
                binding.textInputEditTextLocation,
            )
        )
        // if updated changes, update the whole list
        vm.user.observe(viewLifecycleOwner) { u ->
            //val skills = vm.skills.value
            //if (it != null) {
            //    vm.setSkills(skills?.map { s -> if (it.category != s.category) s else it })
            //    // display a snackbar with the message
            //    //val snack = Snackbar.make(binding.root, "Skill edited", Snackbar.LENGTH_SHORT)
            //    //snack.show()
            //    // change the message for the show profile fragment
            //    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
            //    // reset value of updated to null
            //    vm.resetUpdated()
            //}
            binding.textInputEditTextName.setText(u?.name ?: "null")
            binding.textInputEditTextSurname.setText(u?.surname ?: "null")
            binding.textInputEditTextNickname.setText(u?.nickname ?: "null")
            binding.textInputEditTextBio.setText(u?.bio ?: "null")
            binding.textInputEditTextPhone.setText(u?.phone ?: "null")
            binding.textInputEditTextEmail.setText(u?.email ?: "null")
            binding.textInputEditTextLocation.setText(u?.location ?: "null")

            binding.editableSkillsLayout.removeAllViews()
            u?.apply {
                skills
                    .sortedWith(compareBy({ !it.active }, { it.category }))
                    .map { s -> SkillCard(requireContext(), s, vm, true, editTextChildren) }
                    .forEach { sc: SkillCard -> binding.editableSkillsLayout.addView(sc) }
            }
            // TODO: message when skill changed, see above in the comment
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //private fun observeViewModel() {
    //    vm.name.observe(viewLifecycleOwner) { binding.textInputEditTextName.setText(it) }
    //    vm.surname.observe(viewLifecycleOwner) { binding.textInputEditTextSurname.setText(it) }
    //    vm.nickname.observe(viewLifecycleOwner) { binding.textInputEditTextNickname.setText(it) }
    //    vm.bio.observe(viewLifecycleOwner) { binding.textInputEditTextBio.setText(it) }
    //    vm.phone.observe(viewLifecycleOwner) { binding.textInputEditTextPhone.setText(it) }
    //    vm.email.observe(viewLifecycleOwner) { binding.textInputEditTextEmail.setText(it) }
    //    vm.location.observe(viewLifecycleOwner) { binding.textInputEditTextLocation.setText(it) }
    //    vm.skills.observe(viewLifecycleOwner) {
    //        binding.editableSkillsLayout.removeAllViews()
    //        it
    //            .sortedByDescending { it.active }
    //            .map { s -> SkillCard(requireContext(), s, vm, true) }
    //            .forEach { sc: SkillCard -> binding.editableSkillsLayout.addView(sc) }
    //    }
    //}

    private fun addFocusChangeListeners() {
        binding.textInputEditTextName.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.name.toString() != binding.textInputEditTextName.text.toString()) {
                    vm.user.value?.let {
                        vm.updateUserField(
                            "name",
                            binding.textInputEditTextName.text.toString()
                        )
                    }
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextSurname.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.surname.toString() != binding.textInputEditTextSurname.text.toString()) {
                    vm.user.value?.let {
                        vm.updateUserField(
                            "surname",
                            binding.textInputEditTextSurname.text.toString()
                        )
                    }
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextNickname.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.nickname.toString() != binding.textInputEditTextNickname.text.toString()) {
                    vm.user.value?.let {
                        vm.updateUserField(
                            "nickname",
                            binding.textInputEditTextNickname.text.toString()
                        )
                    }
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextBio.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.bio.toString() != binding.textInputEditTextBio.text.toString()) {
                    vm.user.value?.let {
                        vm.updateUserField(
                            "bio",
                            binding.textInputEditTextBio.text.toString()
                        )
                    }
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextPhone.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.phone.toString() != binding.textInputEditTextPhone.text.toString()) {
                    vm.user.value?.let {
                        vm.updateUserField(
                            "phone",
                            binding.textInputEditTextPhone.text.toString()
                        )
                    }
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextEmail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.email.toString() != binding.textInputEditTextEmail.text.toString()) {
                    vm.user.value?.let {
                        vm.updateUserField(
                            "email",
                            binding.textInputEditTextEmail.text.toString()
                        )
                    }
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextLocation.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.location.toString() != binding.textInputEditTextLocation.text.toString()) {
                    vm.user.value?.let {
                        vm.updateUserField(
                            "location",
                            binding.textInputEditTextLocation.text.toString()
                        )
                    }
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
    }

}