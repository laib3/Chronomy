package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestoreException
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileInfoBinding

import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.viewModel.TimeslotViewModel
import it.polito.mainactivity.viewModel.UserProfileViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ShowProfileInfoFragment(val timeslotId: String?) : Fragment() {
    private var _binding: FragmentShowProfileInfoBinding? = null
    private val binding get() = _binding!!
    private val vmUser: UserProfileViewModel by activityViewModels()
    private val vmTimeslots: TimeslotViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowProfileInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val balanceTextView: TextView = binding.textBalance
        val bioTextView: TextView = binding.textBio
        val phoneTextView: TextView = binding.textPhone
        val locationTextView: TextView = binding.textLocation
        val emailTextView: TextView = binding.textEmail

        Log.d("SHOWPROFILE", timeslotId?.toString()?:"null")
        // If show profile of other users
        if (timeslotId != null) {
            val publisherId =
                vmTimeslots.timeslots.value?.find { t -> t.timeslotId == timeslotId }?.publisher?.get("userId") as String
            // get publisher asynchronously and update
            MainScope().launch {
                val publisher = vmUser.getUserById(publisherId)
                    ?: throw FirebaseFirestoreException(
                        "publisher not found",
                        FirebaseFirestoreException.Code.NOT_FOUND
                    )
                balanceTextView.text = String.format(
                    getString(R.string.user_profile_balance_placeholder),
                    publisher.balance
                )
                bioTextView.text =
                    String.format(getString(R.string.user_profile_bio_placeholder), publisher.bio)
                phoneTextView.text = publisher.phone
                locationTextView.text = publisher.location
                emailTextView.text = publisher.email
            }
        } else { // if show profile of the current publisher
            // observe viewModel changes
            vmUser.user.observe(viewLifecycleOwner) {
                balanceTextView.text =
                    String.format(getString(R.string.user_profile_balance_placeholder), it?.balance)
                bioTextView.text = String.format(
                    getString(R.string.user_profile_bio_placeholder),
                    it?.bio ?: "null"
                )
                phoneTextView.text = it?.phone ?: "null"
                locationTextView.text = it?.location ?: "null"
                emailTextView.text = it?.email ?: "null"
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}