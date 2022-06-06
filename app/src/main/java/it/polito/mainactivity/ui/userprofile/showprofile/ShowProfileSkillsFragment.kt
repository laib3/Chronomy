package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.FirebaseFirestoreException
import it.polito.mainactivity.databinding.FragmentShowProfileSkillsBinding

import it.polito.mainactivity.ui.userprofile.SkillCard
import it.polito.mainactivity.viewModel.TimeslotViewModel
import it.polito.mainactivity.viewModel.UserProfileViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ShowProfileSkillsFragment(val userId: String?) : Fragment() {
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
        if (userId != null) {
            //get publisher asynchronously and update
            MainScope().launch {
                val publisher = vmUser.getUserById(userId) ?: throw FirebaseFirestoreException(
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