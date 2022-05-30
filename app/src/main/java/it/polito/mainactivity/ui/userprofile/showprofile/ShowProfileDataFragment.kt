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
import it.polito.mainactivity.viewModel.TimeslotViewModel
import it.polito.mainactivity.ui.userprofile.SkillCard
import it.polito.mainactivity.viewModel.UserProfileViewModel

class ShowProfileDataFragment : Fragment() {

    private val vmUser: UserProfileViewModel by activityViewModels()
    private val vmTimeslots: TimeslotViewModel by activityViewModels()
    private var _binding: FragmentShowProfileDataBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowProfileDataBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val balanceTextView: TextView = binding.textBalance
        val bioTextView: TextView = binding.textBio
        val phoneTextView: TextView = binding.textPhone
        val locationTextView: TextView = binding.textLocation
        val emailTextView: TextView = binding.textEmail
        val skillsLayout = binding.skillsLayout

        val id = parentFragment?.arguments?.getString("id")

        // If show profile of other users
        if (id != null) {
            val user = vmTimeslots.timeslots.value?.find { t -> t.timeslotId == id }?.user
            balanceTextView.text =
                String.format(getString(R.string.user_profile_balance_placeholder), user?.balance)
            bioTextView.text = String.format(
                getString(R.string.user_profile_bio_placeholder),
                user?.bio ?: "null"
            )
            phoneTextView.text = user?.phone ?: "null"
            locationTextView.text = user?.location ?: "null"
            emailTextView.text = user?.email ?: "null"
            skillsLayout.removeAllViews()
            user?.apply {
                skills.map { s -> SkillCard(requireContext(), s, vmUser, false) }
                    .forEach { sc: SkillCard -> skillsLayout.addView(sc) }
            }
        } else { // if show profile of the current user
            // observe viewModel changes
            vmUser.user.observe(viewLifecycleOwner) {
                balanceTextView.text =
                    String.format(getString(R.string.user_profile_balance_placeholder), it?.balance)
                bioTextView.text = String.format(
                    getString(R.string.user_profile_bio_placeholder),
                    it?.bio ?: "null"
                )
                phoneTextView.text = it?.phone ?: "null"
                locationTextView.text = it?.location ?: "null"
                emailTextView.text = it?.email ?: "null"
                skillsLayout.removeAllViews()
                it?.apply {
                    skills.map { s -> SkillCard(requireContext(), s, vmUser, false) }
                        .forEach { sc: SkillCard -> skillsLayout.addView(sc) }
                }
            }
        }

        return root
    }

}