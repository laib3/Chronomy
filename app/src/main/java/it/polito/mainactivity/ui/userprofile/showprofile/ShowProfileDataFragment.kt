package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import it.polito.mainactivity.databinding.FragmentShowProfileDataBinding

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

        val publisherId = parentFragment?.arguments?.getString("publisherId")

        val tabLayout = binding.tlUserData
        val viewPager = binding.vpUserData

        // pas publisherId through bundle
        val adapter = ShowProfileAdapter(requireActivity(), publisherId)

        val tabTitles = listOf("info","skills","ratings")

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout,viewPager) {
            tab, position -> tab.text = tabTitles[position]
        }.attach()
        return root
    }


}
class ShowProfileAdapter(fa: FragmentActivity, private val publisherId: String?) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val args = Bundle()
        args.putString("publisherId", publisherId)
        return when(position) {
            0 -> ShowProfileInfoFragment().also{it.arguments = args}
            1 -> ShowProfileSkillsFragment().also{it.arguments = args}
            2 -> ShowProfileRatingsFragment().also{it.arguments = args}
            else -> ShowProfileInfoFragment().also{it.arguments = args}
        }
    }
}