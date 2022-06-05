package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestoreException
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileInfoBinding
import it.polito.mainactivity.databinding.FragmentShowProfileSkillsBinding

import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.ui.userprofile.SkillCard
import it.polito.mainactivity.viewModel.TimeslotViewModel
import it.polito.mainactivity.viewModel.UserProfileViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ShowProfileSkillsFragment(val timeslotId: String?) : Fragment() {
    private var _binding: FragmentShowProfileSkillsBinding? = null
    private val binding get() = _binding!!
    private val vmUser: UserProfileViewModel by activityViewModels()
    private val vmTimeslots: TimeslotViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowProfileSkillsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val skillsLayout = binding.skillsLayout

        // If show profile of other users
        if (timeslotId != null) {
            val publisherId =
                vmTimeslots.timeslots.value?.find { t -> t.timeslotId == timeslotId }?.publisher?.get(
                    "userId"
                ) as String
            //get publisher asynchronously and update
            MainScope().launch {
                val publisher = vmUser.getUserById(publisherId) ?: throw FirebaseFirestoreException(
                    "publisher not found",
                    FirebaseFirestoreException.Code.NOT_FOUND
                )
                skillsLayout.removeAllViews()
                publisher.apply {
                    skills.map { s -> SkillCard(requireContext(), s, vmUser, false) }
                        .forEach { sc: SkillCard -> skillsLayout.addView(sc) }
                }
            }
        } else { // if show profile of the current publisher
            //observe viewModel changes
            vmUser.user.observe(viewLifecycleOwner) {
                skillsLayout.removeAllViews()
                it?.apply {
                    skills.map { s -> SkillCard(requireContext(), s, vmUser, false) }
                        .forEach { sc: SkillCard -> skillsLayout.addView(sc) }
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}