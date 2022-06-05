package it.polito.mainactivity.ui.userprofile.showprofile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ShowProfileAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ShowProfileInfoFragment()
            1 -> ShowProfileSkillsFragment()
            2 -> ShowProfileRatingsFragment()
            else -> ShowProfileInfoFragment()
        }
    }
}