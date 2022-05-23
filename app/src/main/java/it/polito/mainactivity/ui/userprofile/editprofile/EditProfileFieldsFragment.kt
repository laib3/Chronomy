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

        // observeViewModel()
        addFocusChangeListeners()

        // if updated changes, update the whole list

        vm.user.observe(viewLifecycleOwner) {
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
            binding.textInputEditTextName.setText(it?.name ?: "null")
            binding.textInputEditTextSurname.setText(it?.surname ?: "null")
            binding.textInputEditTextNickname.setText(it?.nickname ?: "null")
            binding.textInputEditTextBio.setText(it?.bio ?: "null")
            binding.textInputEditTextPhone.setText(it?.phone ?: "null")
            binding.textInputEditTextEmail.setText(it?.email ?: "null")
            binding.textInputEditTextLocation.setText(it?.location ?: "null")

            binding.editableSkillsLayout.removeAllViews()
            it?.apply{ skills
                .sortedByDescending { it.active }
                .map { s -> SkillCard(requireContext(), s, vm, true) }
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
                    vm.user.value?.let { vm.updateTimeslotField(vm.user.value!!.userId, "name", binding.textInputEditTextName.text.toString())}
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextSurname.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.surname.toString() != binding.textInputEditTextSurname.text.toString()) {
                    vm.user.value?.let { vm.updateTimeslotField(vm.user.value!!.userId, "surname", binding.textInputEditTextSurname.text.toString())}
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextNickname.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.nickname.toString() != binding.textInputEditTextNickname.text.toString()) {
                    vm.user.value?.let { vm.updateTimeslotField(vm.user.value!!.userId, "nickname", binding.textInputEditTextNickname.text.toString())}
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextBio.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.bio.toString() != binding.textInputEditTextBio.text.toString()) {
                    vm.user.value?.let { vm.updateTimeslotField(vm.user.value!!.userId, "bio", binding.textInputEditTextBio.text.toString())}
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextPhone.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.phone.toString() != binding.textInputEditTextPhone.text.toString()) {
                    vm.user.value?.let { vm.updateTimeslotField(vm.user.value!!.userId, "phone", binding.textInputEditTextPhone.text.toString())}
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextEmail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.email.toString() != binding.textInputEditTextEmail.text.toString()) {
                    vm.user.value?.let { vm.updateTimeslotField(vm.user.value!!.userId, "email", binding.textInputEditTextEmail.text.toString())}
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
        binding.textInputEditTextLocation.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                if (vm.user.value?.location.toString() != binding.textInputEditTextLocation.text.toString()) {
                    vm.user.value?.let { vm.updateTimeslotField(vm.user.value!!.userId, "location", binding.textInputEditTextLocation.text.toString())}
                    (parentFragment as EditProfileFragment).notifyMessageEditedProfile()
                }

            }
        }
    }

}