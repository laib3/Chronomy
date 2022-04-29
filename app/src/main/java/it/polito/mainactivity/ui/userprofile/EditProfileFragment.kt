package it.polito.mainactivity.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import it.polito.mainactivity.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(UserProfileViewModel::class.java)

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textEditProfile
        homeViewModel.name.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}