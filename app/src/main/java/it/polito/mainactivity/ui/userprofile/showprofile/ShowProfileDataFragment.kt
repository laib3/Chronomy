package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileDataBinding
import it.polito.mainactivity.ui.userprofile.SkillCard
import it.polito.mainactivity.ui.userprofile.UserProfileViewModel

class ShowProfileDataFragment: Fragment() {

    private val vm: UserProfileViewModel by activityViewModels()
    private var _binding: FragmentShowProfileDataBinding? = null
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
        val skillsLayout = binding.skillsLayout

        // observe viewModel changes
        vm.balance.observe(viewLifecycleOwner) { balanceTextView.text = String.format(getString(R.string.user_profile_balance_placeholder), it) }
        vm.bio.observe(viewLifecycleOwner) { bioTextView.text = String.format(getString(R.string.user_profile_bio_placeholder), it) }
        vm.phone.observe(viewLifecycleOwner) { phoneTextView.text = it }
        vm.location.observe(viewLifecycleOwner) { locationTextView.text = it }
        vm.email.observe(viewLifecycleOwner) { emailTextView.text = it }
        vm.skills.observe(viewLifecycleOwner){
            skillsLayout.removeAllViews()
            it
                .map{ s -> SkillCard(requireContext(), s, vm, false) }
                .forEach{ sc: SkillCard -> skillsLayout.addView(sc) }
        }

        return root
    }

}