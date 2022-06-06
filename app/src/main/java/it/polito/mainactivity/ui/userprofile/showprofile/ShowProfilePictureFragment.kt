package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.squareup.picasso.Picasso
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfilePictureBinding
import it.polito.mainactivity.viewModel.TimeslotViewModel
import it.polito.mainactivity.viewModel.UserProfileViewModel

class ShowProfilePictureFragment : Fragment() {

    private val vmUser: UserProfileViewModel by activityViewModels()
    private val vmTimeslots: TimeslotViewModel by activityViewModels()
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

        val id = parentFragment?.arguments?.getString("id")

        // If show profile of other users
        if (id != null) {
            val publisher = vmTimeslots.timeslots.value?.find { t -> t.timeslotId == id }?.publisher ?: throw Exception("ShowProfilePictureFragment: publisher shouldn't be null")
            nameTextView.text = publisher["name"] as String
            surnameTextView.text = publisher["surname"] as String
            nicknameTextView.text =
                String.format(
                    getString(R.string.user_profile_nickname_placeholder),
                    publisher["nickname"] as String
                )
            publisher["profilePictureUrl"]?.also{(publisher["profilePictureUrl"] as String).apply { Picasso.get().load(this).into(profilePicture) }}

        } else { // if show profile of the current publisher
            // observe viewModel changes
            vmUser.user.observe(viewLifecycleOwner) {
                nameTextView.text = it?.name ?: "null"
                surnameTextView.text = it?.surname ?: "null"
                nicknameTextView.text =
                    String.format(
                        getString(R.string.user_profile_nickname_placeholder),
                        it?.nickname
                    )
                it?.profilePictureUrl?.apply { Picasso.get().load(this).into(profilePicture) }

            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}