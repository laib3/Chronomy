package it.polito.mainactivity.ui.userprofile

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileBinding

class ShowProfileFragment : Fragment() {

    private val userProfileViewModel:UserProfileViewModel by activityViewModels()
    private var _binding: FragmentShowProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true)
        _binding = FragmentShowProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.profilePicture.clipToOutline = true

        observeViewModel()
        addSkillCards()

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.showprofile_menu, menu)
    }

    // navigate to edit profile fragment
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel(){
        val nameTextView: TextView = binding.textName
        val surnameTextView: TextView = binding.textSurname
        val nicknameTextView: TextView = binding.textNickname
        val balanceTextView: TextView = binding.textBalance
        val bioTextView: TextView = binding.textBio
        val phoneTextView: TextView = binding.textPhone
        val locationTextView: TextView = binding.textLocation
        val emailTextView: TextView = binding.textEmail
        val profilePicture: ImageView = binding.profilePicture

        userProfileViewModel.name.observe(viewLifecycleOwner) { nameTextView.text = it }
        userProfileViewModel.surname.observe(viewLifecycleOwner) { surnameTextView.text = it }
        userProfileViewModel.nickname.observe(viewLifecycleOwner) {
            nicknameTextView.text =
                String.format(getString(R.string.user_profile_nickname_placeholder), it)
        }
        userProfileViewModel.balance.observe(viewLifecycleOwner) {
            balanceTextView.text =
                String.format(getString(R.string.user_profile_balance_placeholder), it)
        }
        userProfileViewModel.bio.observe(viewLifecycleOwner) {
            bioTextView.text = String.format(getString(R.string.user_profile_bio_placeholder), it)
        }
        userProfileViewModel.phone.observe(viewLifecycleOwner) { phoneTextView.text = it }
        userProfileViewModel.location.observe(viewLifecycleOwner) { locationTextView.text = it }
        userProfileViewModel.email.observe(viewLifecycleOwner) { emailTextView.text = it }
        userProfileViewModel.picture.observe(viewLifecycleOwner)
            { if(it != null) profilePicture.setImageDrawable(it) }

    }

    private fun addSkillCards(){
        val skillsLayout = binding.skillsLayout
        val skills = userProfileViewModel.skills
        skills.forEach{ s ->
            val sc = SkillCard(requireContext(), this, s)
            skillsLayout?.addView(sc)
        }
    }

}