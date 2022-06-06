package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileDataBinding
import it.polito.mainactivity.viewModel.TimeslotViewModel
import it.polito.mainactivity.ui.userprofile.SkillCard
import it.polito.mainactivity.viewModel.UserProfileViewModel
import kotlinx.coroutines.*

class ShowProfileDataFragment : Fragment() {

    // private val vmUser: UserProfileViewModel by activityViewModels()
    // private val vmTimeslots: TimeslotViewModel by activityViewModels()
    private var _binding: FragmentShowProfileDataBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowProfileDataBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val timeslotId = parentFragment?.arguments?.getString("id")
        val publisherId = parentFragment?.arguments?.getString("publisherId")

        val tabLayout = binding.tlUserData
        val viewPager = binding.vpUserData
        val adapter = ShowProfileAdapter(requireActivity(), timeslotId, publisherId)

        val tabTitles = listOf("info","skills","ratings")

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout,viewPager) {
            tab, position -> tab.text = tabTitles[position]
        }.attach()
        return root
    }


}
class ShowProfileAdapter(fa: FragmentActivity, val timeslotId: String?, val userId: String?) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ShowProfileInfoFragment(userId)
            1 -> ShowProfileSkillsFragment(userId)
            2 -> ShowProfileRatingsFragment(timeslotId)
            else -> ShowProfileInfoFragment(userId)
        }
    }
}