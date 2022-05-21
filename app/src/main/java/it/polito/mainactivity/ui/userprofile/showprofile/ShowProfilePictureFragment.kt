package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfilePictureBinding
import it.polito.mainactivity.ui.userprofile.UserProfileViewModel

class ShowProfilePictureFragment: Fragment() {

    private val vm: UserProfileViewModel by activityViewModels()
    private var _binding: FragmentShowProfilePictureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowProfilePictureBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val nameTextView: TextView = binding.textName
        val surnameTextView: TextView = binding.textSurname
        val nicknameTextView: TextView = binding.textNickname
        val profilePicture: ImageView = binding.profilePicture

        profilePicture.clipToOutline = true

        // observe viewModel changes
        vm.user.observe(viewLifecycleOwner){
            nameTextView.text = it?.name ?: "null"
            surnameTextView.text = it?.surname ?: "null"
            nicknameTextView.text = String.format(getString(R.string.user_profile_nickname_placeholder), it?.nickname)
            //TODO: observe also picture
            //if(it.picture != null) profilePicture.setImageDrawable(it.picture)

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}