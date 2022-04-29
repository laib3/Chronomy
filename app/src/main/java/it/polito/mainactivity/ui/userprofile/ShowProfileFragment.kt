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
import org.w3c.dom.Text

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
        val balanceTextView: TextView = binding.textBalance
        val bioTextView: TextView = binding.textBio
        val phoneTextView: TextView = binding.textPhone
        val locationTextView: TextView = binding.textLocation
        val emailTextView: TextView = binding.textEmail
        userProfileViewModel.name.observe(viewLifecycleOwner) { nameTextView.text = it }
        userProfileViewModel.surname.observe(viewLifecycleOwner){ surnameTextView.text = it }
        userProfileViewModel.nickname.observe(viewLifecycleOwner){ nicknameTextView.text = String.format(getString(R.string.user_profile_nickname_placeholder), it) }
        userProfileViewModel.balance.observe(viewLifecycleOwner){ balanceTextView.text = String.format(getString(R.string.user_profile_balance_placeholder), it)}
        userProfileViewModel.bio.observe(viewLifecycleOwner){ bioTextView.text = String.format(getString(R.string.user_profile_bio_placeholder), it)}
        userProfileViewModel.phone.observe(viewLifecycleOwner){ phoneTextView.text = it }
        userProfileViewModel.location.observe(viewLifecycleOwner){ locationTextView.text = it }
        userProfileViewModel.email.observe(viewLifecycleOwner){ emailTextView.text = it }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}