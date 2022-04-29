package it.polito.mainactivity.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileBinding

class ShowProfileFragment : Fragment() {

    private var _binding: FragmentShowProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userProfileViewModel =
            ViewModelProvider(this).get(UserProfileViewModel::class.java)

        _binding = FragmentShowProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val nameTextView: TextView = binding.textName
        val surnameTextView: TextView = binding.textSurname
        val nicknameTextView: TextView = binding.textNickname
        userProfileViewModel.name.observe(viewLifecycleOwner) { nameTextView.text = it }
        userProfileViewModel.surname.observe(viewLifecycleOwner){ surnameTextView.text = it }
        userProfileViewModel.nickname.observe(viewLifecycleOwner){ nicknameTextView.text = String.format(getString(R.string.user_profile_nickname_placeholder), it) }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}