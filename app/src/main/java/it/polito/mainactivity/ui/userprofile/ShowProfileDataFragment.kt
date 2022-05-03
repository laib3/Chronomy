package it.polito.mainactivity.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileBinding
import it.polito.mainactivity.databinding.FragmentShowProfileDataBinding

class ShowProfileDataFragment: Fragment() {

    private val userProfileViewModel:UserProfileViewModel by activityViewModels()
    private var _binding: FragmentShowProfileDataBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentShowProfileDataBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val balanceTextView: TextView = binding.textBalance
        val bioTextView: TextView = binding.textBio
        val phoneTextView: TextView = binding.textPhone
        val locationTextView: TextView = binding.textLocation
        val emailTextView: TextView = binding.textEmail

        // observe viewModel changes
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

        // add skills
        val skillsLayout = binding.skillsLayout
        val skills = userProfileViewModel.skills
        skills.forEach{ s ->
            val sc = SkillCard(requireContext(), this, s)
            skillsLayout?.addView(sc)
        }

        return root
    }


}