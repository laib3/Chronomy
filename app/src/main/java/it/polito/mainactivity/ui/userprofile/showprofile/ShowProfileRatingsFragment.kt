package it.polito.mainactivity.ui.userprofile.showprofile

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestoreException
import it.polito.mainactivity.MainActivity
import it.polito.mainactivity.R
import it.polito.mainactivity.databinding.FragmentShowProfileInfoBinding
import it.polito.mainactivity.databinding.FragmentShowProfileRatingsBinding
import it.polito.mainactivity.databinding.FragmentShowProfileSkillsBinding

import it.polito.mainactivity.model.Utils
import it.polito.mainactivity.ui.userprofile.SkillCard
import it.polito.mainactivity.viewModel.TimeslotViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ShowProfileRatingsFragment(val timeslotId: String?) : Fragment() {
    private var _binding: FragmentShowProfileRatingsBinding? = null
    private val binding get() = _binding!!
    private val vmTimeslots: TimeslotViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowProfileRatingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // If show profile of other users
        if (timeslotId != null) {

        } else { // if show profile of the current publisher

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}